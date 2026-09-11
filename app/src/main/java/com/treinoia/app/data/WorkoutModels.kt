package com.treinoia.app.data

/** Dados preenchidos pelo usuário no formulário. */
data class WorkoutRequest(
    val objetivo: String,
    val nivel: String,
    val diasPorSemana: Int,
    val focoHoje: String,
    val equipamentos: List<String>,
    val restricoes: String,
    val duracaoMin: Int
)

/** Um exercício dentro do treino gerado. */
data class Exercise(
    val nome: String,
    val series: Int,
    val repeticoes: String,
    val descansoSegundos: Int,
    val observacao: String?
)

/** Treino completo retornado pela IA. */
data class Workout(
    val titulo: String,
    val resumo: String,
    val exercicios: List<Exercise>
)

enum class Objetivo(val valor: String, val label: String) {
    HIPERTROFIA("hipertrofia", "Hipertrofia (ganho de massa)"),
    FORCA("forca", "Força"),
    EMAGRECIMENTO("emagrecimento", "Emagrecimento / definição"),
    RESISTENCIA("resistencia", "Resistência muscular"),
    CONDICIONAMENTO("condicionamento_geral", "Condicionamento geral"),
}

enum class Nivel(val valor: String, val label: String) {
    INICIANTE("iniciante", "Iniciante (0-6 meses)"),
    INTERMEDIARIO("intermediario", "Intermediário (6 meses-2 anos)"),
    AVANCADO("avancado", "Avançado (2+ anos)"),
}

enum class FocoTreino(val valor: String, val label: String, val badge: String) {
    CORPO_TODO("corpo_todo", "Corpo todo (full body)", "FULL BODY"),
    SUPERIOR("superior", "Superior (peito, costas, ombro, braço)", "SUPERIOR"),
    INFERIOR("inferior", "Inferior (pernas, glúteo)", "INFERIOR"),
    PUSH("push", "Push (peito, ombro, tríceps)", "PUSH"),
    PULL("pull", "Pull (costas, bíceps)", "PULL"),
    PEITO_TRICEPS("peito_triceps", "Peito e tríceps", "PEITO/TRÍCEPS"),
    COSTAS_BICEPS("costas_biceps", "Costas e bíceps", "COSTAS/BÍCEPS"),
    PERNAS("pernas", "Pernas", "PERNAS"),
    OMBRO_ABDOMEN("ombro_abdomen", "Ombro e abdômen", "OMBRO/ABS"),
}

data class Equipamento(val valor: String, val label: String, val padraoSelecionado: Boolean)

val EQUIPAMENTOS_DISPONIVEIS = listOf(
    Equipamento("barra", "Barra", true),
    Equipamento("halteres", "Halteres", true),
    Equipamento("maquinas", "Máquinas", true),
    Equipamento("cabos", "Cabos/polia", true),
    Equipamento("kettlebell", "Kettlebell", false),
    Equipamento("smith", "Smith", false),
)
