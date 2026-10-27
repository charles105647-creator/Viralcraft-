package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class ScriptCategory(val title: String, val subtitle: String, val iconTag: String) {
    MINI_NOVELA("Mini Novelas", "Dramas 9:16 com reviravoltas e cliffhangers", "drama"),
    CINEMA_FILMS("Filmes & Séries", "Recaps dinâmicos, análises e teorias de cinema", "movie"),
    ANIMATION_CARTOON("Desenhos Animados", "Animação 3D, fábulas infantis e contos", "cartoon"),
    STORIES_MYSTERY("Estórias & Relatos", "Histórias reais do Reddit, mistério e lendas", "story"),
    BIKES_CYCLING("Bicicletas & Grau", "Grau 244, manobras, montagem e ciclismo", "bike"),
    ECOMMERCE("Achadinhos & Vendas", "Conversão direta e links de afiliados", "ecommerce"),
    UNBOXING("Unboxing & Review", "ASMR, abertura de pacotes e testes de resistência", "unboxing")
}

data class ScriptTemplatePreset(
    val id: String,
    val category: ScriptCategory,
    val title: String,
    val badge: String,
    val retentionHook: String,
    val plotSetup: String,
    val sceneStructure: List<String>,
    val mentalTrigger: String,
    val defaultTone: String,
    val hashtags: String
)

data class GeneratedVideoScript(
    val title: String,
    val hook: String,
    val problem: String,
    val solution: String,
    val offer: String,
    val cta: String,
    val fullScript: String,
    val captionTikTok: String,
    val captionShopee: String,
    val captionInstagram: String,
    val hashtags: String
)

data class ScriptVariation(
    val id: String = java.util.UUID.randomUUID().toString(),
    val styleTitle: String,
    val triggerType: String,
    val hook: String,
    val problem: String,
    val solution: String,
    val offer: String,
    val cta: String,
    val fullScript: String,
    val estimatedDurationSeconds: Int = 30,
    val recommendedHashtags: String = "#achadinhos #shopee #tiktokmademebuyit #review",
    val tiktokCaption: String = "",
    val shopeeCaption: String = "",
    val instagramCaption: String = ""
)

object ScriptTemplatesCatalog {
    val MINI_NOVELA_TEMPLATES = listOf(
        ScriptTemplatePreset(
            id = "novela_vinganca_garcom",
            category = ScriptCategory.MINI_NOVELA,
            title = "A Vingança do Garçom Humilhado",
            badge = "🔥 98% Retenção",
            retentionHook = "Ele jogou a comida no chão e me mandou limpar... sem saber que eu era o dono do restaurante!",
            plotSetup = "Cliente arrogante humilha atendente humilde em público. No clímax, a gerência geral entra com os documentos provando a verdadeira identidade.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Gancho brutal do prato quebrando e a ordem humilhante",
                "Cena 2 (3-12s): O atendente recolhe com calma enquanto o cliente ri com os amigos",
                "Cena 3 (12-22s): O segurança-chefe chega e entrega as chaves da franquia ao atendente",
                "Cena 4 (22-30s): Choque do cliente e lição de vida com convite para o Episódio 2"
            ),
            mentalTrigger = "Justiça & Revelação Dramática",
            defaultTone = "Drama & Tensão Emocional",
            hashtags = "#mininovelavirais #dramatiktok #historiasreais #vinganca #novela916"
        ),
        ScriptTemplatePreset(
            id = "novela_chefe_disfarcado",
            category = ScriptCategory.MINI_NOVELA,
            title = "O Bilionário Disfarçado de Faxineiro",
            badge = "⭐ Viral Em Alta",
            retentionHook = "Nenhum funcionário quis me ajudar a carregar a caixa pesada, exceto a jovem do primeiro dia de estágio!",
            plotSetup = "Presidente da empresa testa o caráter da equipe usando roupas simples. Apenas uma estagiária demonstra empatia genuína.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Homem idoso derruba pastas no chão do saguão corporativo",
                "Cena 2 (3-11s): Executivos ignoram e pisam nos papéis; estagiária se ajoelha para ajudar",
                "Cena 3 (11-20s): O idoso tira o boné e convoca a reunião da diretoria no andar 45",
                "Cena 4 (20-30s): Promoção relâmpago ao vivo e demissão dos arrogantes"
            ),
            mentalTrigger = "Empatia & Reconhecimento",
            defaultTone = "Inspirador & Dramático",
            hashtags = "#chefedisfarcado #licoesdevida #novelinhas #retencaomaxima #viral"
        ),
        ScriptTemplatePreset(
            id = "novela_segredo_sogra",
            category = ScriptCategory.MINI_NOVELA,
            title = "O Bilhete Secreto na Encomenda",
            badge = "👀 Suspense Extremo",
            retentionHook = "Abri a encomenda que chegou no nome do meu marido e encontrei um bilhete que mudou tudo...",
            plotSetup = "Esposa desconfiada descobre envelope misterioso escondido no fundo da caixa, iniciando investigação rápida com final surpreendente.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Corte rápido abrindo a caixa lacrada e encontrando a carta vermelha",
                "Cena 2 (3-10s): Leitura dos primeiros trechos revelando um encontro secreto às 19h",
                "Cena 3 (10-22s): Ela vai até o endereço e encontra a festa surpresa de aniversário",
                "Cena 4 (22-30s): Alívio cômico e convite para ver os presentes nos comentários"
            ),
            mentalTrigger = "Curiosidade & Reviravolta",
            defaultTone = "Suspense & Mistério",
            hashtags = "#suspense #curiosidade #historias #fofoca #reviravolta"
        ),
        ScriptTemplatePreset(
            id = "novela_heranca_esquecida",
            category = ScriptCategory.MINI_NOVELA,
            title = "A Filha Rejeitada e a Herança Secreta",
            badge = "💎 Emoção & Lágrimas",
            retentionHook = "Os irmãos dividiram todos os carros de luxo e deixaram apenas uma caixa velha de madeira para ela...",
            plotSetup = "Irmãos gananciosos descartam objeto simples na partilha de bens, mas dentro havia o título da fazenda principal.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Irmãos zombando e saindo com as chaves dos carros novos",
                "Cena 2 (3-11s): Ela abre a caixa com lágrimas nos olhos e encontra o fundo falso",
                "Cena 3 (11-20s): Certidão de propriedade com carimbo do cartório de 5 milhões",
                "Cena 4 (20-30s): Mensagem final da avó premiando quem amava de verdade"
            ),
            mentalTrigger = "Superação & Triunfo",
            defaultTone = "Emocionante & Comovente",
            hashtags = "#superacao #heranca #novelascurtas #drama916 #emocionante"
        )
    )

    val UNBOXING_TEMPLATES = listOf(
        ScriptTemplatePreset(
            id = "unboxing_asmr_sensorial",
            category = ScriptCategory.UNBOXING,
            title = "Unboxing ASMR Sensorial Hipnótico",
            badge = "🎧 100% Satisfatório",
            retentionHook = "Coloque os fones de ouvido! Esse som de plástico rasgando vai arrepiar seu cérebro em 3 segundos...",
            plotSetup = "Abertura minuciosa de pacote com microfonação ultra próxima. Foco em texturas, estalos de plástico bolha e toque frio do produto metálico.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Corte cirúrgico com estilete bem devagar (som amplificado)",
                "Cena 2 (3-12s): Puxada suave do plástico de proteção revelando o brilho espelhado",
                "Cena 3 (12-22s): Teste tátil dos botões com som de clique gostoso",
                "Cena 4 (22-30s): Teste de ligar o visor e indicação de onde encontrar com desconto"
            ),
            mentalTrigger = "Sensação Sensorial & Satisfação",
            defaultTone = "ASMR & Sussurrado Hipnótico",
            hashtags = "#asmrunboxing #asmrbrasil #satisfatorio #oddlysatisfying #tech"
        ),
        ScriptTemplatePreset(
            id = "unboxing_teste_resistencia",
            category = ScriptCategory.UNBOXING,
            title = "Teste Extremo de Resistência & Destruição",
            badge = "💥 Ação & Impacto",
            retentionHook = "O fabricante jurou que isso é indestrutível! Vamos jogar do terceiro andar pra ver se é verdade!",
            plotSetup = "Desafio físico ao vivo para testar alegações mirabolantes do anúncio. Demonstração de durabilidade real sem corte.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Arremesso de impacto na parede ou queda ao vivo",
                "Cena 2 (3-10s): Aproximação dramática da câmera para checar se quebrou ou riscou",
                "Cena 3 (10-20s): Teste 2 com água ou pressão forte para comprovar blindagem",
                "Cena 4 (20-30s): Nota 10 em durabilidade e link com garantia no carrinho"
            ),
            mentalTrigger = "Prova Real & Curiosidade Extrema",
            defaultTone = "Energético & Eletrizante",
            hashtags = "#testederesistencia #indestrutivel #reviewhardcore #unboxingbr #tecnologia"
        ),
        ScriptTemplatePreset(
            id = "unboxing_pacote_misterioso",
            category = ScriptCategory.UNBOXING,
            title = "Pacote Misterioso de R$ 19 da Shopee",
            badge = "📦 Surpresa & Curiosidade",
            retentionHook = "Paguei R$ 19,90 numa caixa perdida de devolução da Shopee e veio algo que vale mais de R$ 300!",
            plotSetup = "Abertura às cegas de pacotes promocionais. Tensão na revelação dos itens camada por camada calculando o valor real de mercado.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Balançando a caixa misteriosa tentando adivinhar o peso e som",
                "Cena 2 (3-10s): Rasgando o papel pardo e encontrando o primeiro acessório",
                "Cena 3 (10-20s): Revelação do item principal na caixa lacrada original",
                "Cena 4 (20-30s): Cálculo do lucro ao vivo e dica de como comprar esses lotes"
            ),
            mentalTrigger = "Oportunidade & Caça ao Tesouro",
            defaultTone = "Empolgado & Eufórico",
            hashtags = "#pacotemisterioso #shopee #achadinhosshopee #unboxing #lucro"
        ),
        ScriptTemplatePreset(
            id = "unboxing_barato_vs_caro",
            category = ScriptCategory.UNBOXING,
            title = "Barato vs Grife (R$ 35 vs R$ 1.200)",
            badge = "⚖️ Comparativo Choque",
            retentionHook = "Coloquei a réplica perfeita de R$ 35 lado a lado com o original de R$ 1.200. Ninguém acertou a diferença!",
            plotSetup = "Comparativo lado a lado de acabamento, peso, som e utilidade prática. Prova que o custo-benefício compensa 10 vezes mais.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Apresentação dos dois produtos idênticos sem revelar qual é o caro",
                "Cena 2 (3-12s): Comparação macro de costura, plástico e conexões",
                "Cena 3 (12-22s): Teste de funcionamento prático mostrando empate técnico",
                "Cena 4 (22-30s): Veredito final mandando economizar seu dinheiro clicando no link"
            ),
            mentalTrigger = "Economia Inteligente & Comparação",
            defaultTone = "Investigativo & Revelador",
            hashtags = "#baratovscaro #comparativo #reviewhonesto #achadinhos #economizar"
        )
    )

    val CINEMA_FILMS_TEMPLATES = listOf(
        ScriptTemplatePreset(
            id = "cinema_final_explicado",
            category = ScriptCategory.CINEMA_FILMS,
            title = "O Final Explicado Que Explodiu Cabeças",
            badge = "🍿 Plot Twist 9:16",
            retentionHook = "Se você assistiu esse filme e não entendeu nada do final, senta aí que eu vou te mostrar o detalhe nos primeiros 5 minutos que você perdeu!",
            plotSetup = "Análise cirúrgica de reviravolta em filme de suspense/ficção científica. Conecta pistas sutis com o desfecho chocante.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Clímax acelerado com música de suspense e interrogação na tela",
                "Cena 2 (3-12s): Volta para a cena inicial apontando o reflexo no espelho",
                "Cena 3 (12-22s): Revelação de que o protagonista e o vilão eram a mesma pessoa",
                "Cena 4 (22-30s): Pergunta desafiadora para comentar se você notou ou caiu no golpe"
            ),
            mentalTrigger = "Curiosidade Intelectual & Revelação",
            defaultTone = "Cinematográfico & Misterioso",
            hashtags = "#filmeseseries #finalexplicado #cinema #plottwist #filmesdeterror #dicasdefilmes"
        ),
        ScriptTemplatePreset(
            id = "cinema_cena_epica_bastidores",
            category = ScriptCategory.CINEMA_FILMS,
            title = "A Cena Mais Cara do Cinema Mundial",
            badge = "🔥 Ação & Adrenalina",
            retentionHook = "O diretor destruiu 40 carros esportivos de verdade nessa cena porque se recusou a usar computação gráfica!",
            plotSetup = "Bastidores inacreditáveis de superprodução de ação. Segredos de dublês, explosões reais e perigo nos sets de filmagem.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Explosão massiva de veículos em câmera lenta",
                "Cena 2 (3-11s): Corte para a câmera dos bastidores mostrando os dublês reais",
                "Cena 3 (11-20s): O valor absurdo gasto em apenas 38 segundos de filme",
                "Cena 4 (20-30s): Veredito se valeu a pena e enquete se você já assistiu"
            ),
            mentalTrigger = "Choque Visual & Fatos Curiosos",
            defaultTone = "Épico & Eletrizante",
            hashtags = "#cenasdefilmes #bastidores #acao #hollywood #adrenalina #curiosidades"
        ),
        ScriptTemplatePreset(
            id = "cinema_filme_banido_curiosidade",
            category = ScriptCategory.CINEMA_FILMS,
            title = "O Filme Proibido Que Quase Ninguém Conhece",
            badge = "⚠️ Mistério Oculto",
            retentionHook = "Esse filme foi banido em 12 países por ser considerado perturbador demais... e o motivo vai te deixar sem dormir hoje!",
            plotSetup = "Descoberta de thriller psicológico raro e intrigante. Instiga a curiosidade sem entregar spoilers gratuitos.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Notícia de proibição com trilha sombria e data de lançamento",
                "Cena 2 (3-10s): Sinopse rápida do experimento social retratado na história",
                "Cena 3 (10-22s): Por que os governos tentaram censurar a distribuição internacional",
                "Cena 4 (22-30s): Onde encontrar para assistir no streaming hoje mesmo"
            ),
            mentalTrigger = "Proibido & Exclusividade",
            defaultTone = "Sombrio & Provocativo",
            hashtags = "#filmeproibido #suspense #misterio #cinemaobscuro #terrorpsicologico"
        )
    )

    val ANIMATION_CARTOON_TEMPLATES = listOf(
        ScriptTemplatePreset(
            id = "cartoon_fabula_moderna",
            category = ScriptCategory.ANIMATION_CARTOON,
            title = "A Raposa e o Cristal do Tempo",
            badge = "🦊 Animação 3D Mágica",
            retentionHook = "A pequena raposa encontrou uma engrenagem brilhante na floresta e tudo que ela tocava voltava 10 segundos no tempo!",
            plotSetup = "Aventura encantadora em animação dinâmica com lição de empatia e responsabilidade para todas as idades.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Personagem encontra o cristal que emite faíscas coloridas",
                "Cena 2 (3-11s): Teste divertido voltando o tempo antes de uma xícara cair",
                "Cena 3 (11-21s): O grande perigo quando o cristal começa a sugar o próprio vilarejo",
                "Cena 4 (21-30s): A escolha corajosa e convite para a Parte 2 nos comentários"
            ),
            mentalTrigger = "Fantasia & Encantamento Lúdico",
            defaultTone = "Mágico & Aventureiro",
            hashtags = "#desenhoanimado #animacao3d #historiasinfantis #cartoon #pixarstyle"
        ),
        ScriptTemplatePreset(
            id = "cartoon_teoria_bizarra",
            category = ScriptCategory.ANIMATION_CARTOON,
            title = "A Teoria Oculta dos Desenhos da Infância",
            badge = "🧠 Teoria Viral",
            retentionHook = "Você cresceu assistindo esse desenho toda manhã e nunca percebeu a mensagem sinistra escondida na casa do vizinho!",
            plotSetup = "Desconstrução bem-humorada e intrigante de detalhes escondidos em animações clássicas de TV.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Zoom acelerado num detalhe do cenário da animação clássica",
                "Cena 2 (3-12s): Comparação entre 3 episódios diferentes com a mesma pista",
                "Cena 3 (12-22s): O que os criadores confirmaram numa entrevista de 2005",
                "Cena 4 (22-30s): Sua infância arruinada em 30 segundos! Deixe sua opinião"
            ),
            mentalTrigger = "Nostalgia & Quebra de Paradigma",
            defaultTone = "Nostálgico & Conspiratório",
            hashtags = "#teoriasdedesenhos #nostalgia #desenhosantigos #curiosidades #animacao"
        ),
        ScriptTemplatePreset(
            id = "cartoon_origem_vilao",
            category = ScriptCategory.ANIMATION_CARTOON,
            title = "O Vilão do Desenho Tinha Razão?",
            badge = "⚖️ Debate Viral",
            retentionHook = "Quando somos crianças torcemos pelo herói, mas quando crescemos percebemos que o vilão era o único com bom senso!",
            plotSetup = "Análise inteligente das motivações do antagonista do desenho, mostrando que sua atitude tinha fundamento lógico.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Frase icônica do vilão que todo mundo julgava errado",
                "Cena 2 (3-10s): O herói causando mais destruição na cidade do que o vilão",
                "Cena 3 (10-21s): Os argumentos que ninguém prestou atenção na época",
                "Cena 4 (21-30s): Votação na tela: Você concorda com o vilão ou com o herói?"
            ),
            mentalTrigger = "Controvérsia Saudável & Engajamento",
            defaultTone = "Crítico & Bem-Humorado",
            hashtags = "#viloes #desenhos #animacao #debatedesenhos #viraltiktok"
        )
    )

    val STORIES_MYSTERY_TEMPLATES = listOf(
        ScriptTemplatePreset(
            id = "story_relato_reddit_porta",
            category = ScriptCategory.STORIES_MYSTERY,
            title = "Relato Anônimo: A Porta do Corredor",
            badge = "📖 Relato Real",
            retentionHook = "Aluguei um apartamento no centro e a única regra do proprietário era nunca, jamais abrir a terceira porta do corredor...",
            plotSetup = "Storytelling imersivo baseado em relatos virais da internet. Curiosidade crescente e reviravolta no desfecho.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Porta trancada com chave enferrujada e barulho de arranhão",
                "Cena 2 (3-12s): Dias se passam até a curiosidade falar mais alto às 2 da manhã",
                "Cena 3 (12-22s): O momento exato que a maçaneta gira por dentro",
                "Cena 4 (22-30s): A revelação do segredo e o aviso urgente para o espectador"
            ),
            mentalTrigger = "Tensão Psicológica & Mistério",
            defaultTone = "Imersivo & Suspense Noturno",
            hashtags = "#historiasdeterror #relatosdoreddit #casosreais #misterio #creepypastabr"
        ),
        ScriptTemplatePreset(
            id = "story_superacao_real",
            category = ScriptCategory.STORIES_MYSTERY,
            title = "Do Zero ao Milhão: A História do Jovem da Favela",
            badge = "💪 Motivação Pura",
            retentionHook = "Ele vendia paçoca no semáforo embaixo de chuva aos 16 anos... Hoje ele é dono da fábrica que comprou o semáforo!",
            plotSetup = "Biografia rápida e comovente de resiliência, disciplina e vitória contra todas as probabilidades.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Foto antiga de dificuldade com frase de impacto",
                "Cena 2 (3-10s): A rotina de acordar às 4h da manhã para estudar e trabalhar",
                "Cena 3 (10-21s): A grande virada de chave no primeiro negócio digital",
                "Cena 4 (21-30s): Mensagem inspiradora para quem tá pensando em desistir hoje"
            ),
            mentalTrigger = "Inspiração & Resiliência",
            defaultTone = "Inspirador & Triunfante",
            hashtags = "#superacao #motivacional #historiasreais #empreendedorismo #vencer"
        ),
        ScriptTemplatePreset(
            id = "story_ligacao_perdida",
            category = ScriptCategory.STORIES_MYSTERY,
            title = "A Ligação Perdida da Própria Voz",
            badge = "📞 Arrepiante",
            retentionHook = "Recebi 14 chamadas perdidas do meu próprio número às 3:33 da madrugada... e quando atendi, ouvi minha própria voz!",
            plotSetup = "Enigma de mistério com tecnologia e arrepios, ideal para vídeos com narração lo-fi e retenção máxima até o final.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Tela do smartphone tocando com o próprio nome na tela",
                "Cena 2 (3-12s): O sussurro que dizia exatamente o que ia acontecer às 8h",
                "Cena 3 (12-22s): A coincidência assustadora que se confirmou pela manhã",
                "Cena 4 (22-30s): Você teria coragem de atender? Deixe nos comentários"
            ),
            mentalTrigger = "Medo do Desconhecido & Curiosidade",
            defaultTone = "Sussurrado & Arrepiante",
            hashtags = "#creepypasta #historiasdeterror #sobrenatural #misterioso #arrepios"
        )
    )

    val BIKES_CYCLING_TEMPLATES = listOf(
        ScriptTemplatePreset(
            id = "bike_grau_raspar_rabeta",
            category = ScriptCategory.BIKES_CYCLING,
            title = "O Segredo Para Raspar a Rabeta no Grau de Bike",
            badge = "🚲 Grau de Rua 244",
            retentionHook = "Se a sua bike tá caindo pro lado na hora de puxar o grau, você tá cometendo esse erro clássico no freio traseiro!",
            plotSetup = "Tutorial de alta adrenalina sobre domínio do ponto de equilíbrio, ajuste da pinça hidráulica e postura no guidão.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Bike empinada raspando no asfalto com faíscas e som alto",
                "Cena 2 (3-11s): Demonstração em câmera lenta da posição do dedo no manete",
                "Cena 3 (11-21s): O truque da calibragem do pneu traseiro com menos libras",
                "Cena 4 (21-30s): Desafio para marcar o amigo que vive caindo da bike"
            ),
            mentalTrigger = "Adrenalina & Maestria Técnica",
            defaultTone = "Rua & Adrenalina Pura",
            hashtags = "#graudebike #grau244 #bikeestilo #244naoecrime #ciclismo #bikeporn"
        ),
        ScriptTemplatePreset(
            id = "bike_cubo_barulhento",
            category = ScriptCategory.BIKES_CYCLING,
            title = "Cubo Barulhento vs Cubo Silencioso (Som da Catraca)",
            badge = "🔊 ASMR de Rua",
            retentionHook = "Aumenta o volume do celular! Olha o estalo desse cubo K7 barulhento na descida... Parece uma metralhadora!",
            plotSetup = "Demonstração e comparação sonora de componentes de bike de alta performance. O som viciante da catraca que viraliza no TikTok.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Roda girando no suporte com som ensurdecedor da catraca",
                "Cena 2 (3-10s): Comparação lado a lado com cubo comum de fábrica silencioso",
                "Cena 3 (10-21s): Por que esse cubo dá mais resposta na hora de puxar manobra",
                "Cena 4 (21-30s): Link das peças no carrinho e enquete: Barulhento ou Silencioso?"
            ),
            mentalTrigger = "Sensação Sonora & Customização",
            defaultTone = "Empolgado & Descontraído",
            hashtags = "#cubobarulhento #bikelovers #grau #mtbbrasil #pecasdebike #bikecustom"
        ),
        ScriptTemplatePreset(
            id = "bike_montando_do_zero",
            category = ScriptCategory.BIKES_CYCLING,
            title = "Montei a Bike dos Sonhos com Apenas R$ 800",
            badge = "🛠️ Reforma & Transformação",
            retentionHook = "Achei um quadro enferrujado no ferro-velho por R$ 50 e transformei nessa máquina com freio hidráulico e pintura camaleão!",
            plotSetup = "Antes e depois satisfatório de restauração e montagem mecânica de bike urbana com peças de melhor custo-benefício.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): O quadro sujo e quebrado contrastando com o resultado reluzente",
                "Cena 2 (3-11s): Lixando, pintando com spray metálico e montando a suspensão",
                "Cena 3 (11-21s): Instalação do kit de freios a disco e rodas aro 29",
                "Cena 4 (21-30s): Teste de rodagem na rua e lista de onde comprei cada peça"
            ),
            mentalTrigger = "Transformação & Economia Criativa",
            defaultTone = "Criativo & Mão na Massa",
            hashtags = "#reformadebike #montagemdebike #façavocêmesmo #grau #ciclismo"
        )
    )

    val ECOMMERCE_TEMPLATES = listOf(
        ScriptTemplatePreset(
            id = "ecom_achadinho_urgente",
            category = ScriptCategory.ECOMMERCE,
            title = "Achadinho Secreto da Shopee que Baixou 70%",
            badge = "🛒 Oferta Relâmpago",
            retentionHook = "Se você ainda compra isso em loja física pagando caro, para tudo e olha essa promoção que acabou de vazar!",
            plotSetup = "Produto prático com desconto agressivo, demonstrando utilidade imediata e escassez de cupom.",
            sceneStructure = listOf(
                "Cena 1 (0-3s): Apresentação dinâmica do produto resolvendo problema comum",
                "Cena 2 (3-10s): Comparação de preço da loja física vs cupom da Shopee",
                "Cena 3 (10-20s): Demonstração das 3 funções principais em uso real",
                "Cena 4 (20-30s): Chamada para clicar no link da bio ou carrinho com frete grátis"
            ),
            mentalTrigger = "Urgência & Oportunidade Única",
            defaultTone = "Energético & Vendedor",
            hashtags = "#achadinhosshopee #shopeebrasil #comprinhas #promocao #cupom"
        )
    )

    fun getTemplatesForCategory(category: ScriptCategory): List<ScriptTemplatePreset> {
        return when (category) {
            ScriptCategory.MINI_NOVELA -> MINI_NOVELA_TEMPLATES
            ScriptCategory.CINEMA_FILMS -> CINEMA_FILMS_TEMPLATES
            ScriptCategory.ANIMATION_CARTOON -> ANIMATION_CARTOON_TEMPLATES
            ScriptCategory.STORIES_MYSTERY -> STORIES_MYSTERY_TEMPLATES
            ScriptCategory.BIKES_CYCLING -> BIKES_CYCLING_TEMPLATES
            ScriptCategory.ECOMMERCE -> ECOMMERCE_TEMPLATES
            ScriptCategory.UNBOXING -> UNBOXING_TEMPLATES
        }
    }
}


object GeminiScriptService {
    private const val TAG = "GeminiScriptService"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateEcommerceScript(
        productName: String,
        category: String,
        keyFeatures: String,
        originalPrice: Double,
        promoPrice: Double,
        coupon: String,
        targetAudience: String,
        avatarTone: String
    ): GeneratedVideoScript = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    Você é o maior especialista em vídeos curtos (9:16) de alta conversão para Shopee Vídeo, TikTok Shop, Instagram Reels e Facebook Reels.
                    Crie um roteiro viral em Português do Brasil para um avatar virtual com clonagem de voz vender o seguinte produto físico:

                    PRODUTO: $productName
                    CATEGORIA: $category
                    CARACTERÍSTICAS: $keyFeatures
                    PREÇO ORIGINAL: R$ ${String.format("%.2f", originalPrice)}
                    PREÇO PROMOCIONAL: R$ ${String.format("%.2f", promoPrice)}
                    CUPOM: ${if (coupon.isNotBlank()) coupon else "Sem cupom adicional"}
                    PÚBLICO-ALVO: $targetAudience
                    TOM DO AVATAR: $avatarTone

                    Responda ESTRITAMENTE em formato JSON com o seguinte schema:
                    {
                      "title": "Título atrativo do vídeo",
                      "hook": "Gancho magnético de 0 a 3 segundos para reter atenção imediata",
                      "problem": "Problema ou frustração que a pessoa passa sem o produto (3 a 10s)",
                      "solution": "Demonstração e diferenciais do produto (10 a 22s)",
                      "offer": "Preço promocional e cupom com gatilho de escassez (22 a 30s)",
                      "cta": "Chamada para ação direta mandando clicar no carrinho ou link da bio (30 a 35s)",
                      "captionTikTok": "Legenda otimizada para TikTok",
                      "captionShopee": "Legenda com tags de cupom para Shopee Vídeo",
                      "captionInstagram": "Legenda atraente para Reels",
                      "hashtags": "#achadinhos #shopee #viral #review"
                    }
                """.trimIndent()

                val jsonBody = JSONObject().apply {
                    val contents = JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            })
                        })
                    }
                    put("contents", contents)
                    put("generationConfig", JSONObject().apply {
                        put("responseMimeType", "application/json")
                        put("temperature", 0.7)
                    })
                }

                val request = Request.Builder()
                    .url("$BASE_URL/$MODEL:generateContent?key=$apiKey")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val rootJson = JSONObject(bodyString)
                    val candidates = rootJson.optJSONArray("candidates")
                    val firstCandidate = candidates?.optJSONObject(0)
                    val content = firstCandidate?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text") ?: ""

                    if (text.isNotBlank()) {
                        val parsed = JSONObject(text)
                        val hook = parsed.optString("hook")
                        val problem = parsed.optString("problem")
                        val solution = parsed.optString("solution")
                        val offer = parsed.optString("offer")
                        val cta = parsed.optString("cta")
                        val fullScript = "$hook $problem $solution $offer $cta"

                        return@withContext GeneratedVideoScript(
                            title = parsed.optString("title", "Vídeo Viral de $productName"),
                            hook = hook,
                            problem = problem,
                            solution = solution,
                            offer = offer,
                            cta = cta,
                            fullScript = fullScript,
                            captionTikTok = parsed.optString("captionTikTok"),
                            captionShopee = parsed.optString("captionShopee"),
                            captionInstagram = parsed.optString("captionInstagram"),
                            hashtags = parsed.optString("hashtags", "#achadinhos #viral #shopee #review")
                        )
                    }
                } else {
                    Log.w(TAG, "Gemini API error code: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error invoking Gemini API, using high-converting fallback", e)
            }
        }

        // High-converting algorithmic fallback script generator:
        createAlgorithmicScript(
            productName = productName,
            category = category,
            keyFeatures = keyFeatures,
            originalPrice = originalPrice,
            promoPrice = promoPrice,
            coupon = coupon
        )
    }

    suspend fun generatePersuasiveSalesScriptVariations(
        productDescription: String,
        productName: String = "",
        mentalTrigger: String = "Urgência & Escassez",
        durationSeconds: Int = 30,
        tone: String = "Empolgado & Viral",
        targetAudience: String = "Compradores online que buscam custo-benefício"
    ): List<ScriptVariation> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    Você é o copywriter número 1 do mundo especializado em scripts de venda altamente persuasivos para vídeos verticais (9:16) no TikTok Shop, Shopee Vídeo, Instagram Reels e Facebook Reels.
                    Sua missão é transformar a descrição do seguinte item em 3 variações irresistíveis de roteiro de vendas para ser narrado por um avatar/locutor virtual:

                    DESCRIÇÃO DO ITEM:
                    $productDescription

                    NOME DO PRODUTO: ${productName.ifBlank { "Identifique ou dê um nome atraente para o produto com base na descrição" }}
                    GATILHO MENTAL PRINCIPAL: $mentalTrigger
                    DURAÇÃO APROXIMADA: $durationSeconds segundos
                    TOM DE VOZ: $tone
                    PÚBLICO-ALVO: $targetAudience

                    Crie 3 abordagens distintas de altíssima conversão de vendas:
                    - Variação 1: Focada em Gatilho de Urgência & Escassez extrema (últimas unidades, desconto relâmpago).
                    - Variação 2: Focada em Curiosidade & "Achadinho Secreto que quase ninguém conhece" (retenção máxima nos primeiros 3 segundos).
                    - Variação 3: Focada em Dor Diária vs. Transformação/Alívio Imediato com Prova Social.

                    Responda ESTRITAMENTE em formato JSON com o seguinte schema:
                    {
                      "variations": [
                        {
                          "styleTitle": "Nome descritivo da abordagem (ex: 🔥 Urgência & Escassez)",
                          "triggerType": "Urgência",
                          "hook": "Gancho magnético de 0 a 3 segundos para reter atenção imediata",
                          "problem": "Problema ou frustração que a pessoa enfrenta sem o item (3 a 10s)",
                          "solution": "Como o produto soluciona de forma mágica e rápida os pontos da descrição (10 a 20s)",
                          "offer": "Oferta imperdível e motivo para comprar agora mesmo (20 a 25s)",
                          "cta": "Chamada para ação clara mandando clicar no carrinho laranja ou link da bio (25 a 30s)",
                          "tiktokCaption": "Legenda persuasiva com emojis para TikTok",
                          "shopeeCaption": "Legenda com cupom e frete grátis para Shopee Vídeo",
                          "instagramCaption": "Legenda para Reels convidando para comentar ou clicar na bio",
                          "recommendedHashtags": "#achadinhos #shopee #viral #review #produtosvirais"
                        }
                      ]
                    }
                """.trimIndent()

                val jsonBody = JSONObject().apply {
                    val contents = JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            })
                        })
                    }
                    put("contents", contents)
                    put("generationConfig", JSONObject().apply {
                        put("responseMimeType", "application/json")
                        put("temperature", 0.75)
                    })
                }

                val request = Request.Builder()
                    .url("$BASE_URL/$MODEL:generateContent?key=$apiKey")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val rootJson = JSONObject(bodyString)
                    val candidates = rootJson.optJSONArray("candidates")
                    val firstCandidate = candidates?.optJSONObject(0)
                    val content = firstCandidate?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text") ?: ""

                    if (text.isNotBlank()) {
                        val parsed = JSONObject(text)
                        val varsArray = parsed.optJSONArray("variations")
                        if (varsArray != null && varsArray.length() > 0) {
                            val results = mutableListOf<ScriptVariation>()
                            for (i in 0 until varsArray.length()) {
                                val item = varsArray.optJSONObject(i) ?: continue
                                val hook = item.optString("hook")
                                val problem = item.optString("problem")
                                val solution = item.optString("solution")
                                val offer = item.optString("offer")
                                val cta = item.optString("cta")
                                val full = "$hook $problem $solution $offer $cta"

                                results.add(
                                    ScriptVariation(
                                        styleTitle = item.optString("styleTitle", "Abordagem Persuasiva ${i + 1}"),
                                        triggerType = item.optString("triggerType", mentalTrigger),
                                        hook = hook,
                                        problem = problem,
                                        solution = solution,
                                        offer = offer,
                                        cta = cta,
                                        fullScript = full,
                                        estimatedDurationSeconds = durationSeconds,
                                        recommendedHashtags = item.optString("recommendedHashtags", "#achadinhos #viral #shopee"),
                                        tiktokCaption = item.optString("tiktokCaption"),
                                        shopeeCaption = item.optString("shopeeCaption"),
                                        instagramCaption = item.optString("instagramCaption")
                                    )
                                )
                            }
                            if (results.isNotEmpty()) {
                                return@withContext results
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini sales script variations call failed, falling back", e)
            }
        }

        // High-converting algorithmic fallback variations based on the product description
        createFallbackVariations(productDescription, productName, mentalTrigger, durationSeconds)
    }

    private fun createFallbackVariations(
        description: String,
        productName: String,
        mentalTrigger: String,
        durationSeconds: Int
    ): List<ScriptVariation> {
        val resolvedName = productName.ifBlank {
            description.split(" ").take(4).joinToString(" ").replace(Regex("[^A-Za-z0-9 À-ÿ]"), "").trim().ifBlank { "Esse Produto Incrível" }
        }

        val snippet = description.take(120).trim().ifBlank { "qualidade superior e praticidade no dia a dia" }

        val var1 = ScriptVariation(
            styleTitle = "🔥 Urgência Extrema & Escassez Relâmpago",
            triggerType = "Urgência & Escassez",
            hook = "PARE O QUE ESTÁ FAZENDO! O lote com mais de 50% de desconto desse produto acabou de ser liberado!",
            problem = "Quantas vezes você comprou algo barato na internet e se arrependeu porque quebrou na primeira semana?",
            solution = "Com o $resolvedName você tem $snippet. Testado e aprovado em durabilidade e desempenho real!",
            offer = "O fornecedor oficial liberou hoje com frete grátis e desconto acumulativo com cupom no carrinho!",
            cta = "Toca rápido no botão laranja aqui embaixo antes que as unidades da promoção acabem!",
            fullScript = "PARE O QUE ESTÁ FAZENDO! O lote com mais de 50% de desconto desse produto acabou de ser liberado! Quantas vezes você comprou algo barato na internet e se arrependeu porque quebrou na primeira semana? Com o $resolvedName você tem $snippet. Testado e aprovado em durabilidade e desempenho real! O fornecedor oficial liberou hoje com frete grátis e desconto acumulativo com cupom no carrinho! Toca rápido no botão laranja aqui embaixo antes que as unidades da promoção acabem!",
            estimatedDurationSeconds = durationSeconds,
            recommendedHashtags = "#achadinhos #shopeebr #urgente #descontos #ofertas",
            tiktokCaption = "🚨 NÃO COMPRE outro antes de ver isso! $resolvedName com mega desconto no carrinho!",
            shopeeCaption = "🔥 ACHADINHO SHOPEE: $resolvedName com FRETE GRÁTIS e cupom exclusivo!",
            instagramCaption = "Achadinho aprovado com nota máxima! 🔥 Comente 'EU QUERO' para receber o link com desconto no direct!"
        )

        val var2 = ScriptVariation(
            styleTitle = "👀 Curiosidade / 'Achadinho Secreto'",
            triggerType = "Curiosidade",
            hook = "Eu duvido que você já viu uma utilidade dessas na sua vida! Olha isso aqui!",
            problem = "A maioria das pessoas passa perrengue tentando resolver isso da forma antiga e cansativa.",
            solution = "É exatamente pra isso que serve o $resolvedName! Desenvolvido com foco em $snippet, ele economiza horas do seu tempo.",
            offer = "Pelo que ele entrega, deveria custar o triplo, mas hoje está saindo a preço de custo!",
            cta = "Corre no link da bio ou no carrinho aqui do vídeo e garanta o seu antes que viralize!",
            fullScript = "Eu duvido que você já viu uma utilidade dessas na sua vida! Olha isso aqui! A maioria das pessoas passa perrengue tentando resolver isso da forma antiga e cansativa. É exatamente pra isso que serve o $resolvedName! Desenvolvido com foco em $snippet, ele economiza horas do seu tempo. Pelo que ele entrega, deveria custar o triplo, mas hoje está saindo a preço de custo! Corre no link da bio ou no carrinho aqui do vídeo e garanta o seu antes que viralize!",
            estimatedDurationSeconds = durationSeconds,
            recommendedHashtags = "#viralbrasil #achadinhosincriveis #inovacao #review",
            tiktokCaption = "Você sabia que isso existia?! 😱 Assista até o final! Link no carrinho 🛒",
            shopeeCaption = "✨ O produto mais curioso e útil da Shopee! $resolvedName por preço de banana.",
            instagramCaption = "Salva esse post pra não perder! ✨ Link de compra seguro nos stories e na bio."
        )

        val var3 = ScriptVariation(
            styleTitle = "⭐ Prova Social & Avaliação Sincera",
            triggerType = "Prova Social",
            hook = "Mais de 15 mil pessoas compraram isso esse mês na internet e eu fui obrigado a testar!",
            problem = "Todo mundo estava comentando sobre $snippet e eu achei que era só marketing exagerado.",
            solution = "Mas olha o resultado com o $resolvedName! A entrega foi super rápida, material de altíssima resistência e funcionamento impecável.",
            offer = "Avaliado com nota 4.9 de 5 estrelas e cupom extra ativado para seguidores!",
            cta = "Clica no carrinho abaixo pra conferir os comentários e garantir com garantia total!",
            fullScript = "Mais de 15 mil pessoas compraram isso esse mês na internet e eu fui obrigado a testar! Todo mundo estava comentando sobre $snippet e eu achei que era só marketing exagerado. Mas olha o resultado com o $resolvedName! A entrega foi super rápida, material de altíssima resistência e funcionamento impecável. Avaliado com nota 4.9 de 5 estrelas e cupom extra ativado para seguidores! Clica no carrinho abaixo pra conferir os comentários e garantir com garantia total!",
            estimatedDurationSeconds = durationSeconds,
            recommendedHashtags = "#testedeproduto #reviewsincero #shopee #tiktokshop",
            tiktokCaption = "Testei o produto mais vendido da internet! 🌟 Vale a pena? Clica no carrinho pra ver!",
            shopeeCaption = "🌟 Nota 4.9 na Shopee! Mais de 15.000 avaliações positivas do $resolvedName.",
            instagramCaption = "Review honesto do queridinho da internet! 🌟 Link direto na bio."
        )

        return listOf(var1, var2, var3)
    }

    suspend fun generateGenreScript(
        category: ScriptCategory,
        topicTitle: String,
        details: String = "",
        tone: String = "Viral & Alta Retenção"
    ): GeneratedVideoScript = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val topic = topicTitle.ifBlank {
            when (category) {
                ScriptCategory.MINI_NOVELA -> "A Vingança do Garçom Milionário"
                ScriptCategory.CINEMA_FILMS -> "O Final Explicado de Neon Reckoning"
                ScriptCategory.ANIMATION_CARTOON -> "A Raposa Inventora e o Cristal Mágico"
                ScriptCategory.STORIES_MYSTERY -> "O Mistério da Meia-Noite no Quarto Trancado"
                ScriptCategory.BIKES_CYCLING -> "O Segredo do Ponto de Equilíbrio no Grau de Bike 244"
                ScriptCategory.ECOMMERCE -> "Achadinho Secreto que Baixou 70%"
                ScriptCategory.UNBOXING -> "Unboxing Hipnótico ASMR"
            }
        }

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    Você é um roteirista profissional de vídeos virais curtos (9:16) para TikTok, Instagram Reels, Kwai e YouTube Shorts.
                    Crie um roteiro completo em Português do Brasil para o seguinte gênero e tema:

                    GÊNERO: ${category.title} (${category.subtitle})
                    TEMA DO VÍDEO: $topic
                    DETALHES OU ENREDO: ${details.ifBlank { "Gere um enredo surpreendente com retenção de 95% e gancho nos primeiros 3 segundos." }}
                    TOM DE VOZ: $tone

                    Estruture em 4 partes dinâmicas:
                    1. Gancho (0-3s): Frase impactante com pergunta chocante ou ação rápida que impede o scroll.
                    2. Desenvolvimento / Problema (3-12s): Contexto, tensão ou demonstração com detalhes curiosos.
                    3. Clímax / Revelação (12-24s): A grande reviravolta, o momento da manobra, o final explicado ou o segredo revelado.
                    4. CTA / Chamada (24-30s): Pergunta instigante mandando comentar, curtir ou ver a parte 2.

                    Responda ESTRITAMENTE em formato JSON com o schema:
                    {
                      "title": "Título magnético do vídeo",
                      "hook": "Gancho magnético de 0 a 3 segundos",
                      "problem": "Desenvolvimento e tensão (3 a 12s)",
                      "solution": "Clímax, manobra ou reviravolta (12 a 24s)",
                      "offer": "Ponto alto ou dica de ouro (24 a 27s)",
                      "cta": "Chamada para ação para comentários e compartilhamentos (27 a 30s)",
                      "captionTikTok": "Legenda com emojis para TikTok",
                      "captionShopee": "Legenda para canais de vídeo",
                      "captionInstagram": "Legenda com chamada para salvar",
                      "hashtags": "Hashtags virais do nicho"
                    }
                """.trimIndent()

                val jsonBody = JSONObject().apply {
                    val contents = JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            })
                        })
                    }
                    put("contents", contents)
                    put("generationConfig", JSONObject().apply {
                        put("responseMimeType", "application/json")
                        put("temperature", 0.8)
                    })
                }

                val request = Request.Builder()
                    .url("$BASE_URL/$MODEL:generateContent?key=$apiKey")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val rootJson = JSONObject(bodyString)
                    val candidates = rootJson.optJSONArray("candidates")
                    val firstCandidate = candidates?.optJSONObject(0)
                    val content = firstCandidate?.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text") ?: ""

                    if (text.isNotBlank()) {
                        val parsed = JSONObject(text)
                        val hook = parsed.optString("hook")
                        val problem = parsed.optString("problem")
                        val solution = parsed.optString("solution")
                        val offer = parsed.optString("offer")
                        val cta = parsed.optString("cta")
                        val fullScript = "$hook $problem $solution $offer $cta"

                        return@withContext GeneratedVideoScript(
                            title = parsed.optString("title", topic),
                            hook = hook,
                            problem = problem,
                            solution = solution,
                            offer = offer,
                            cta = cta,
                            fullScript = fullScript,
                            captionTikTok = parsed.optString("captionTikTok"),
                            captionShopee = parsed.optString("captionShopee"),
                            captionInstagram = parsed.optString("captionInstagram"),
                            hashtags = parsed.optString("hashtags", "#viral #fyp #shorts")
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error generating genre script via Gemini API, using fallback", e)
            }
        }

        // Algorithmic genre fallback:
        val preset = ScriptTemplatesCatalog.getTemplatesForCategory(category).firstOrNull()
        val hook = preset?.retentionHook ?: "Você não vai acreditar no que aconteceu quando eu comecei a gravar isso!"
        val problem = preset?.plotSetup ?: "Muita gente comete esse erro ou nunca percebeu esse detalhe impressionante."
        val solution = preset?.sceneStructure?.getOrNull(2) ?: "E foi aí que tudo mudou na velocidade da luz com essa revelação!"
        val offer = "Essa é a melhor técnica comprovada que viraliza em todas as redes."
        val cta = "Comenta o que você achou e já salva esse vídeo antes que o algoritmo suma com ele!"
        val full = "$hook $problem $solution $cta"

        GeneratedVideoScript(
            title = "$topic (${category.title})",
            hook = hook,
            problem = problem,
            solution = solution,
            offer = offer,
            cta = cta,
            fullScript = full,
            captionTikTok = "Olha isso até o final! 😱 $topic! Deixa sua opinião nos comentários! 👇🔥",
            captionShopee = "Dica viral: $topic! Veja mais novidades e se inscreva no canal!",
            captionInstagram = "Surpreendente! ✨ Salva esse post e marca aquele amigo que precisa ver isso hoje! 🚀",
            hashtags = preset?.hashtags ?: "#viral #tiktokbrasil #reels #kwai #shorts"
        )
    }

    private fun createAlgorithmicScript(
        productName: String,
        category: String,
        keyFeatures: String,
        originalPrice: Double,
        promoPrice: Double,
        coupon: String
    ): GeneratedVideoScript {
        val discount = if (originalPrice > 0) {
            (((originalPrice - promoPrice) / originalPrice) * 100).toInt()
        } else 40

        val hook = "Para tudo e não compre outro produto antes de ver isso aqui!"
        val problem = "Se você sofre gastando caro e no final o produto quebra ou não entrega o que promete, você precisa conhecer isso."
        val solution = "Esse é o $productName! Com tecnologia exclusiva, ele conta com $keyFeatures, facilitando seu dia a dia em minutos."
        val offer = if (coupon.isNotBlank()) {
            "Ele tá saindo de R$ ${String.format("%.2f", originalPrice)} por apenas R$ ${String.format("%.2f", promoPrice)}, mais de $discount% de desconto com o cupom $coupon!"
        } else {
            "Ele tá saindo de R$ ${String.format("%.2f", originalPrice)} por apenas R$ ${String.format("%.2f", promoPrice)} com entrega rápida!"
        }
        val cta = "Clica agora no carrinho laranja aqui embaixo ou no link da bio antes que o estoque promocional se esgote!"

        val fullScript = "$hook $problem $solution $offer $cta"

        return GeneratedVideoScript(
            title = "Oferta Relâmpago: $productName ($discount% OFF)",
            hook = hook,
            problem = problem,
            solution = solution,
            offer = offer,
            cta = cta,
            fullScript = fullScript,
            captionTikTok = "Olha o que acabou de chegar! 😱 $productName com $discount% OFF! Corre no carrinho antes de esgotar! 🛒⚡",
            captionShopee = "🔥 ACHADINHO DA SHOPEE: $productName por apenas R$ ${String.format("%.2f", promoPrice)}! Use o cupom $coupon e garanta FRETE GRÁTIS!",
            captionInstagram = "Dica de ouro pra você economizar hoje! ✨ $productName aprovadíssimo no teste! Comenta 'QUERO' que te mando o link direto! 🚀",
            hashtags = "#achadinhos #shopeebr #tiktokmademebuyit #review #produtosvirais #desconto #oferta"
        )
    }
}
