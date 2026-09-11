package com.treinoia.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Erro específico da chamada à IA, com o status HTTP quando disponível (para mensagens amigáveis). */
class ClaudeApiException(message: String, val httpStatus: Int? = null) : Exception(message)

object ClaudeApiClient {

    // claude-haiku-4-5-20251001 também funciona bem aqui (mais barato) se quiser trocar.
    private const val MODEL = "claude-sonnet-5"
    private const val ENDPOINT = "https://api.anthropic.com/v1/messages"
    private const val ANTHROPIC_VERSION = "2023-06-01"
    private const val TOOL_NAME = "gerar_treino"

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .build()

    // Restringe o escopo da IA estritamente a treino de academia.
    private val SYSTEM_INSTRUCTION = """
        Você é um personal trainer especializado exclusivamente em treinos de academia
        (musculação com pesos livres, máquinas, cabos e barras).

        Regras rígidas:
        - Gere APENAS treinos de academia. Nunca sugira cardio ao ar livre, corrida de rua,
          esportes, alongamento isolado, dieta/nutrição, suplementação ou qualquer atividade
          fora do ambiente de academia.
        - Se o usuário pedir algo fora desse escopo, ignore o pedido fora de escopo e gere
          mesmo assim um treino de academia coerente com o resto do contexto.
        - Sempre responda em português do Brasil.
        - Leve a sério objetivo, nível de experiência, equipamentos disponíveis, tempo
          disponível e restrições/lesões informadas — nunca prescreva um exercício que
          agrave uma restrição informada.
        - Ajuste volume (séries x repetições) e intensidade ao nível de experiência:
          iniciantes recebem menos exercícios e mais foco em técnica; avançados recebem
          mais volume e intensidade.
        - Use sempre a ferramenta "$TOOL_NAME" para registrar o treino final. Não responda
          em texto livre.
    """.trimIndent()

    private fun toolInputSchema(): JSONObject {
        val exercicioSchema = JSONObject().apply {
            put("type", "object")
            put("properties", JSONObject().apply {
                put("nome", JSONObject().put("type", "string"))
                put("series", JSONObject().put("type", "integer"))
                put("repeticoes", JSONObject().put("type", "string"))
                put("descanso_segundos", JSONObject().put("type", "integer"))
                put("observacao", JSONObject().put("type", "string"))
            })
            put("required", JSONArray(listOf("nome", "series", "repeticoes", "descanso_segundos")))
        }

        return JSONObject().apply {
            put("type", "object")
            put("properties", JSONObject().apply {
                put("titulo", JSONObject().put("type", "string"))
                put("resumo", JSONObject().put("type", "string"))
                put("exercicios", JSONObject().apply {
                    put("type", "array")
                    put("items", exercicioSchema)
                })
            })
            put("required", JSONArray(listOf("titulo", "resumo", "exercicios")))
        }
    }

    private fun buildUserPrompt(req: WorkoutRequest): String {
        val equipamentosTexto =
            if (req.equipamentos.isEmpty()) "qualquer equipamento padrão de academia"
            else req.equipamentos.joinToString(", ")
        val restricoesTexto = req.restricoes.ifBlank { "nenhuma informada" }

        return """
            Gere um treino de academia com estes dados do usuário:
            - Objetivo: ${req.objetivo}
            - Nível: ${req.nivel}
            - Dias de treino por semana: ${req.diasPorSemana}
            - Foco do treino de hoje: ${req.focoHoje}
            - Equipamentos disponíveis: $equipamentosTexto
            - Restrições/lesões: $restricoesTexto
            - Tempo disponível: ${req.duracaoMin} minutos

            Monte entre 5 e 8 exercícios adequados ao tempo disponível.
        """.trimIndent()
    }

    suspend fun gerarTreino(request: WorkoutRequest, apiKey: String): Workout =
        withContext(Dispatchers.IO) {
            val body = JSONObject().apply {
                put("model", MODEL)
                put("max_tokens", 2048)
                put("system", SYSTEM_INSTRUCTION)
                put("messages", JSONArray().put(JSONObject().apply {
                    put("role", "user")
                    put("content", buildUserPrompt(request))
                }))
                put("tools", JSONArray().put(JSONObject().apply {
                    put("name", TOOL_NAME)
                    put("description", "Registra o treino de academia gerado, já estruturado.")
                    put("input_schema", toolInputSchema())
                }))
                put("tool_choice", JSONObject().apply {
                    put("type", "tool")
                    put("name", TOOL_NAME)
                })
            }

            val httpRequest = Request.Builder()
                .url(ENDPOINT)
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", ANTHROPIC_VERSION)
                .post(body.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = try {
                client.newCall(httpRequest).execute()
            } catch (e: IOException) {
                throw ClaudeApiException("Sem conexão com a internet. Verifique sua rede e tente novamente.")
            }

            response.use { resp ->
                val responseText = resp.body?.string().orEmpty()

                if (!resp.isSuccessful) {
                    throw ClaudeApiException(mensagemErroHttp(resp.code, responseText), resp.code)
                }

                val toolInput = try {
                    val contentBlocks = JSONObject(responseText).getJSONArray("content")
                    var found: JSONObject? = null
                    for (i in 0 until contentBlocks.length()) {
                        val block = contentBlocks.getJSONObject(i)
                        if (block.optString("type") == "tool_use" && block.optString("name") == TOOL_NAME) {
                            found = block.getJSONObject("input")
                            break
                        }
                    }
                    found ?: throw ClaudeApiException("A IA não retornou o treino no formato esperado.")
                } catch (e: ClaudeApiException) {
                    throw e
                } catch (e: Exception) {
                    throw ClaudeApiException("Resposta inesperada da IA.")
                }

                parseWorkout(toolInput)
            }
        }

    private fun parseWorkout(obj: JSONObject): Workout {
        val exerciciosJson = obj.optJSONArray("exercicios") ?: JSONArray()
        val exercicios = (0 until exerciciosJson.length()).map { i ->
            val e = exerciciosJson.getJSONObject(i)
            Exercise(
                nome = e.optString("nome", "Exercício"),
                series = e.optInt("series", 3),
                repeticoes = e.optString("repeticoes", "10"),
                descansoSegundos = e.optInt("descanso_segundos", 60),
                observacao = e.optString("observacao", "").ifBlank { null }
            )
        }

        return Workout(
            titulo = obj.optString("titulo", "Treino de academia"),
            resumo = obj.optString("resumo", ""),
            exercicios = exercicios
        )
    }

    private fun mensagemErroHttp(code: Int, body: String): String {
        val apiMessage = try {
            JSONObject(body).getJSONObject("error").optString("message").takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
        return when (code) {
            401 -> "Chave de API inválida. Verifique sua chave nas configurações."
            403 -> "Chave de API sem permissão para esse modelo. Confira no console da Anthropic."
            429 -> "Limite de uso da API atingido no momento. Espere um pouco e tente de novo."
            in 500..599 -> "O servidor da Anthropic está com instabilidade agora. Tente novamente em instantes."
            else -> apiMessage ?: "Erro ao gerar o treino (HTTP $code)."
        }
    }
}
