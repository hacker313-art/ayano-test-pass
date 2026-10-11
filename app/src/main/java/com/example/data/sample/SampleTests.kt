package com.example.data.sample

import com.example.model.QuestionModel
import com.example.model.TestModel
import java.util.UUID

object SampleTests {

    val sampleNeetTest = TestModel(
        testId = "neet_all_india_mock_01",
        name = "AYANO ALL INDIA NEET CBT MOCK TEST - 01",
        date = "2026-10-05",
        duration = 180,
        totalQuestions = 30, // Default rich mock with representative questions across all 4 subjects
        subjects = listOf("Physics", "Chemistry", "Botany", "Zoology"),
        markingScheme = mapOf("correct" to 4, "wrong" to -1, "unattempted" to 0),
        createdBy = "Admin (Ayano Academic Team)",
        publishedAt = System.currentTimeMillis() - 86400000L,
        isPublished = true
    )

    fun getSampleQuestions(testId: String): List<QuestionModel> {
        return listOf(
            // PHYSICS
            QuestionModel(
                id = "${testId}_q1",
                number = 1,
                text = "A particle starts from the origin at t = 0 with an initial velocity of 5i m/s and moves in the x-y plane with a constant acceleration of (3i + 2j) m/s². What is the y-coordinate of the particle at the moment its x-coordinate is 84 m?",
                options = listOf(
                    "36 m",
                    "24 m",
                    "48 m",
                    "18 m"
                ),
                correctOption = 1,
                solution = "x(t) = ux*t + 0.5*ax*t² => 84 = 5t + 1.5t² => 1.5t² + 5t - 84 = 0 => 3t² + 10t - 168 = 0. Solving quadratic: t = 6 s. Then y(t) = uy*t + 0.5*ay*t² = 0 + 0.5 * 2 * 6² = 36 m.",
                subject = "Physics"
            ),
            QuestionModel(
                id = "${testId}_q2",
                number = 2,
                text = "The ratio of the dimensions of Planck constant and that of moment of inertia is the dimension of:",
                options = listOf(
                    "Frequency",
                    "Velocity",
                    "Time",
                    "Angular momentum"
                ),
                correctOption = 1,
                solution = "[h] = [ML²T⁻¹], [I] = [ML²]. Therefore [h]/[I] = [ML²T⁻¹]/[ML²] = [T⁻¹], which is the dimension of Frequency.",
                subject = "Physics"
            ),
            QuestionModel(
                id = "${testId}_q3",
                number = 3,
                text = "A body of mass 60 kg suspended by two strings making angles of 30° and 60° with the horizontal. Find the tension in each string (g = 10 m/s²).",
                options = listOf(
                    "T1 = 300 N, T2 = 300√3 N",
                    "T1 = 300√3 N, T2 = 300 N",
                    "T1 = 600 N, T2 = 600 N",
                    "T1 = 150 N, T2 = 450 N"
                ),
                correctOption = 2,
                solution = "By Lami's theorem or resolving components along horizontal and vertical: T1 cos 30° = T2 cos 60° => T1 * (√3/2) = T2 * (1/2) => T2 = √3 T1. In vertical direction: T1 sin 30° + T2 sin 60° = mg = 600 N. Substituting T2 gives T1 = 300√3 N and T2 = 300 N.",
                subject = "Physics"
            ),
            QuestionModel(
                id = "${testId}_q4",
                number = 4,
                text = "An ideal heat engine working between temperature TH and TL has efficiency η. If both temperatures are increased by 50 K, the new efficiency:",
                options = listOf(
                    "Increases",
                    "Decreases",
                    "Remains constant",
                    "Becomes 100%"
                ),
                correctOption = 2,
                solution = "Efficiency η = 1 - TL/TH. When both TL and TH increase by same positive amount ΔT: (TL + ΔT)/(TH + ΔT) > TL/TH because TH > TL. Thus 1 - (TL+ΔT)/(TH+ΔT) < 1 - TL/TH. Hence efficiency decreases.",
                subject = "Physics"
            ),
            QuestionModel(
                id = "${testId}_q5",
                number = 5,
                text = "Two charges +q and -q are placed at points (0, 0, -a) and (0, 0, a) respectively. The electric potential at any point on the z = 0 plane is:",
                options = listOf(
                    "Infinite",
                    "Zero",
                    "q / (4πε0 a)",
                    "-q / (4πε0 a)"
                ),
                correctOption = 2,
                solution = "Any point on the z = 0 plane is equidistant from +q at (0, 0, -a) and -q at (0, 0, a). Therefore, V = V1 + V2 = k(q/r) + k(-q/r) = 0.",
                subject = "Physics"
            ),
            QuestionModel(
                id = "${testId}_q6",
                number = 6,
                text = "In Young's double slit experiment, if the separation between the slits is halved and the distance between slits and screen is doubled, the fringe width will be:",
                options = listOf(
                    "Doubled",
                    "Halved",
                    "Quadrupled (4 times)",
                    "Unchanged"
                ),
                correctOption = 3,
                solution = "Fringe width β = λD / d. If D' = 2D and d' = d/2, then β' = λ(2D)/(d/2) = 4(λD/d) = 4β. Fringe width increases 4-fold.",
                subject = "Physics"
            ),
            QuestionModel(
                id = "${testId}_q7",
                number = 7,
                text = "The de Broglie wavelength of an electron accelerated through a potential difference of 100 V is approximately:",
                options = listOf(
                    "1.227 Å",
                    "12.27 Å",
                    "0.123 Å",
                    "0.012 Å"
                ),
                correctOption = 1,
                solution = "λ = 12.27 / √V Å. For V = 100 V, √V = 10. Thus λ = 12.27 / 10 = 1.227 Å.",
                subject = "Physics"
            ),

            // CHEMISTRY
            QuestionModel(
                id = "${testId}_q8",
                number = 8,
                text = "Which of the following molecules has maximum number of lone pairs on the central atom?",
                options = listOf(
                    "SF4",
                    "XeF4",
                    "ClF3",
                    "XeF2"
                ),
                correctOption = 4,
                solution = "Central atom lone pairs: SF4 has 1 lp; XeF4 has 2 lp; ClF3 has 2 lp; XeF2 has 3 lone pairs (linear geometry with 3 equatorial lone pairs). Maximum is in XeF2 (3 lone pairs).",
                subject = "Chemistry"
            ),
            QuestionModel(
                id = "${testId}_q9",
                number = 9,
                text = "The oxidation state of Cr in CrO5 (Chromium pentoxide) is:",
                options = listOf(
                    "+10",
                    "+6",
                    "+5",
                    "+4"
                ),
                correctOption = 2,
                solution = "CrO5 has a butterfly structure with two peroxide linkages (-O-O-) and one oxo bond (=O). Oxidation state: Cr + 4(-1) + 1(-2) = 0 => Cr = +6.",
                subject = "Chemistry"
            ),
            QuestionModel(
                id = "${testId}_q10",
                number = 10,
                text = "For a reaction A -> Products, a plot of log[A] versus time t gives a straight line with a negative slope. The order of the reaction is:",
                options = listOf(
                    "Zero order",
                    "First order",
                    "Second order",
                    "Third order"
                ),
                correctOption = 2,
                solution = "Integrated rate equation for first order: ln[A] = -kt + ln[A]0 or 2.303 log[A] = -kt + constant. Therefore plot of log[A] vs t is linear for first order.",
                subject = "Chemistry"
            ),
            QuestionModel(
                id = "${testId}_q11",
                number = 11,
                text = "Which of the following compounds will give positive Iodoform test upon reaction with I2 and NaOH?",
                options = listOf(
                    "Methanol",
                    "Benzophenone",
                    "Ethanol",
                    "Diethyl ketone"
                ),
                correctOption = 3,
                solution = "Iodoform test is given by compounds containing CH3-C=O or CH3-CH(OH)- group. Ethanol (CH3-CH2-OH) gets oxidized to ethanal (CH3-CHO) and yields yellow CHI3 precipitate.",
                subject = "Chemistry"
            ),
            QuestionModel(
                id = "${testId}_q12",
                number = 12,
                text = "The hybridization and shape of [Ni(CN)4]²⁻ complex ion respectively are:",
                options = listOf(
                    "sp³, tetrahedral",
                    "dsp², square planar",
                    "sp³d, trigonal bipyramidal",
                    "d²sp³, octahedral"
                ),
                correctOption = 2,
                solution = "Ni²⁺ is 3d⁸. CN⁻ is a strong field ligand, forcing pairing of 3d electrons, leaving one empty 3d orbital. Hybridization is dsp² (square planar) and diamagnetic.",
                subject = "Chemistry"
            ),
            QuestionModel(
                id = "${testId}_q13",
                number = 13,
                text = "Which of the following is an example of an amphoteric oxide?",
                options = listOf(
                    "Na2O",
                    "SO2",
                    "Al2O3",
                    "CaO"
                ),
                correctOption = 3,
                solution = "Al2O3 and ZnO react with both acids and bases to produce salt and water, hence are classic amphoteric oxides.",
                subject = "Chemistry"
            ),
            QuestionModel(
                id = "${testId}_q14",
                number = 14,
                text = "Equal moles of H2 and O2 gases are placed in a container with a small pinhole. The ratio of the rates of effusion (rH2 / rO2) is:",
                options = listOf(
                    "1 : 4",
                    "4 : 1",
                    "1 : 16",
                    "16 : 1"
                ),
                correctOption = 2,
                solution = "According to Graham's law: rH2 / rO2 = √(M_O2 / M_H2) = √(32 / 2) = √16 = 4/1.",
                subject = "Chemistry"
            ),

            // BOTANY
            QuestionModel(
                id = "${testId}_q15",
                number = 15,
                text = "The site of perception of light/photoperiod necessary for induction of flowering in plants is:",
                options = listOf(
                    "Shoot apex",
                    "Leaves",
                    "Floral buds",
                    "Roots"
                ),
                correctOption = 2,
                solution = "The leaves are the site of perception of photoperiod. The floral hormone (florigen) synthesized in leaves migrates to the shoot apex to induce flowering.",
                subject = "Botany"
            ),
            QuestionModel(
                id = "${testId}_q16",
                number = 16,
                text = "During non-cyclic photophosphorylation (Z-scheme), the primary electron acceptor from Photosystem II (P680) is:",
                options = listOf(
                    "Plastoquinone",
                    "Pheophytin",
                    "Plastocyanin",
                    "Ferredoxin"
                ),
                correctOption = 2,
                solution = "When PS II absorbs light, electrons are emitted and transferred to the primary electron acceptor, which is pheophytin, before going to plastoquinone.",
                subject = "Botany"
            ),
            QuestionModel(
                id = "${testId}_q17",
                number = 17,
                text = "In a cross between homozygous red-flowered and white-flowered Snapdragon (Antirrhinum majus), all F1 offspring have pink flowers. This is an example of:",
                options = listOf(
                    "Codominance",
                    "Incomplete dominance",
                    "Multiple alleles",
                    "Polygenic inheritance"
                ),
                correctOption = 2,
                solution = "Incomplete dominance occurs when neither allele is completely dominant, resulting in an intermediate pink phenotype in Snapdragon (1:2:1 ratio in F2).",
                subject = "Botany"
            ),
            QuestionModel(
                id = "${testId}_q18",
                number = 18,
                text = "Which of the following cell organelles is bounded by a single membrane?",
                options = listOf(
                    "Mitochondria",
                    "Chloroplast",
                    "Lysosome",
                    "Nucleus"
                ),
                correctOption = 3,
                solution = "Lysosomes, vacuoles, and peroxisomes are single-membrane bound organelles. Mitochondria, chloroplasts, and nucleus possess double membranes.",
                subject = "Botany"
            ),
            QuestionModel(
                id = "${testId}_q19",
                number = 19,
                text = "The stage of meiosis in which crossing over occurs between non-sister chromatids of homologous chromosomes is:",
                options = listOf(
                    "Zygotene",
                    "Pachytene",
                    "Diplotene",
                    "Diakinesis"
                ),
                correctOption = 2,
                solution = "Crossing over is an enzyme-mediated process (recombinase) occurring during the pachytene stage of Prophase I of meiosis.",
                subject = "Botany"
            ),
            QuestionModel(
                id = "${testId}_q20",
                number = 20,
                text = "In C4 plants, the primary CO2 fixation takes place in which cells and catalyzed by which enzyme?",
                options = listOf(
                    "Bundle sheath cells, RuBisCO",
                    "Mesophyll cells, PEP carboxylase",
                    "Mesophyll cells, RuBisCO",
                    "Bundle sheath cells, PEP carboxylase"
                ),
                correctOption = 2,
                solution = "In C4 plants (like maize, sugarcane), primary CO2 fixation occurs in mesophyll cells via PEP carboxylase forming 4-carbon Oxaloacetic acid (OAA).",
                subject = "Botany"
            ),
            QuestionModel(
                id = "${testId}_q21",
                number = 21,
                text = "Double fertilization is a unique event characteristic to:",
                options = listOf(
                    "Gymnosperms",
                    "Angiosperms",
                    "Pteridophytes",
                    "Bryophytes"
                ),
                correctOption = 2,
                solution = "Double fertilization (syngamy + triple fusion resulting in zygote and PEN) is exclusive to angiosperms (flowering plants).",
                subject = "Botany"
            ),
            QuestionModel(
                id = "${testId}_q22",
                number = 22,
                text = "Root hairs develop from which region of the root?",
                options = listOf(
                    "Root cap",
                    "Region of meristematic activity",
                    "Region of elongation",
                    "Region of maturation"
                ),
                correctOption = 4,
                solution = "The epidermal cells from the region of maturation form very fine and delicate, thread-like structures called root hairs.",
                subject = "Botany"
            ),

            // ZOOLOGY
            QuestionModel(
                id = "${testId}_q23",
                number = 23,
                text = "Which of the following hormones is secreted by the corpus luteum and essential for maintaining the endometrium during pregnancy?",
                options = listOf(
                    "Estrogen",
                    "Progesterone",
                    "LH",
                    "FSH"
                ),
                correctOption = 2,
                solution = "The ruptured Graafian follicle transforms into the corpus luteum, which secretes large amounts of progesterone to maintain pregnancy and endometrium.",
                subject = "Zoology"
            ),
            QuestionModel(
                id = "${testId}_q24",
                number = 24,
                text = "Tidal volume (TV) and Expiratory Reserve Volume (ERV) of an athlete are 500 mL and 1000 mL respectively. What will be his Expiratory Capacity (EC)?",
                options = listOf(
                    "1500 mL",
                    "2000 mL",
                    "2500 mL",
                    "1000 mL"
                ),
                correctOption = 1,
                solution = "Expiratory Capacity (EC) = Tidal Volume (TV) + Expiratory Reserve Volume (ERV) = 500 mL + 1000 mL = 1500 mL.",
                subject = "Zoology"
            ),
            QuestionModel(
                id = "${testId}_q25",
                number = 25,
                text = "The part of human brain that plays a major role in regulating body temperature, urge for eating and drinking is:",
                options = listOf(
                    "Cerebellum",
                    "Hypothalamus",
                    "Medulla oblongata",
                    "Corpus callosum"
                ),
                correctOption = 2,
                solution = "The hypothalamus contains centers which control body temperature, urge for eating (hunger) and drinking (thirst), as well as neurosecretory cells.",
                subject = "Zoology"
            ),
            QuestionModel(
                id = "${testId}_q26",
                number = 26,
                text = "In human kidneys, which part of the nephron is impermeable to water but allows active reabsorption of electrolytes?",
                options = listOf(
                    "Proximal Convoluted Tubule (PCT)",
                    "Descending limb of loop of Henle",
                    "Ascending limb of loop of Henle",
                    "Collecting duct"
                ),
                correctOption = 3,
                solution = "The ascending limb of Henle's loop is impermeable to water but permeable to electrolytes (active or passive transport), diluting the filtrate.",
                subject = "Zoology"
            ),
            QuestionModel(
                id = "${testId}_q27",
                number = 27,
                text = "Which of the following antibody is present in highest concentration in human colostrum providing natural passive immunity to newborns?",
                options = listOf(
                    "IgM",
                    "IgG",
                    "IgA",
                    "IgE"
                ),
                correctOption = 3,
                solution = "Colostrum contains abundant IgA antibodies that protect the infant from infections across mucosal linings.",
                subject = "Zoology"
            ),
            QuestionModel(
                id = "${testId}_q28",
                number = 28,
                text = "Sino-atrial node (SAN) is called the pacemaker of human heart because:",
                options = listOf(
                    "It can generate the maximum number of action potentials (70-75/min)",
                    "It is located in the left atrium",
                    "It receives oxygenated blood from pulmonary veins",
                    "It initiates rhythmic ventricular relaxation"
                ),
                correctOption = 1,
                solution = "SAN generates maximum action potentials (70–75 per minute) and is responsible for initiating and maintaining the rhythmic contractile activity of the heart.",
                subject = "Zoology"
            ),
            QuestionModel(
                id = "${testId}_q29",
                number = 29,
                text = "Which of the following is NOT an autoimmune disorder?",
                options = listOf(
                    "Rheumatoid arthritis",
                    "Myasthenia gravis",
                    "Muscular dystrophy",
                    "Systemic lupus erythematosus (SLE)"
                ),
                correctOption = 3,
                solution = "Muscular dystrophy is a genetic disorder (mostly X-linked recessive causing progressive degeneration of skeletal muscle). The others are autoimmune disorders.",
                subject = "Zoology"
            ),
            QuestionModel(
                id = "${testId}_q30",
                number = 30,
                text = "Restriction endonucleases are enzymes that:",
                options = listOf(
                    "Degrade RNA molecules",
                    "Cut DNA at specific recognition nucleotide sequences",
                    "Join DNA fragments together",
                    "Synthesize complementary DNA from mRNA"
                ),
                correctOption = 2,
                solution = "Restriction endonucleases ('molecular scissors') cut DNA at specific palindromic recognition sequences, widely used in recombinant DNA technology.",
                subject = "Zoology"
            )
        )
    }

    const val SAMPLE_PASTE_PDF_PAPER = """
AYANO NEET MOCK TEST 2026
SECTION A - PHYSICS
1. A particle starts from origin with velocity 5i m/s and moves with acceleration (3i + 2j) m/s2. Find y-coordinate when x = 84m.
(1) 36 m
(2) 24 m
(3) 48 m
(4) 18 m

2. The ratio of dimensions of Planck constant and moment of inertia is:
(1) Frequency
(2) Velocity
(3) Time
(4) Angular momentum

3. An ideal heat engine working between TH and TL has efficiency eta. If both temperatures increase by 50K:
(1) Increases
(2) Decreases
(3) Remains constant
(4) Becomes 100%

SECTION B - CHEMISTRY
4. Which molecule has maximum number of lone pairs on the central atom?
(1) SF4
(2) XeF4
(3) ClF3
(4) XeF2

5. Oxidation state of Cr in CrO5 is:
(1) +10
(2) +6
(3) +5
(4) +4

SECTION C - BOTANY
6. The site of perception of light for flowering in plants is:
(1) Shoot apex
(2) Leaves
(3) Floral buds
(4) Roots

7. During non-cyclic photophosphorylation, primary electron acceptor from PSII is:
(1) Plastoquinone
(2) Pheophytin
(3) Plastocyanin
(4) Ferredoxin

SECTION D - ZOOLOGY
8. Hormone secreted by corpus luteum to maintain endometrium:
(1) Estrogen
(2) Progesterone
(3) LH
(4) FSH

9. Part of brain regulating body temperature and hunger:
(1) Cerebellum
(2) Hypothalamus
(3) Medulla oblongata
(4) Pons

10. SAN is called pacemaker because:
(1) Generates maximum action potentials (70-75/min)
(2) Located in left atrium
(3) Connects to AV node
(4) Pumps blood directly

ANSWER KEY
1. (1) 2. (1) 3. (2) 4. (4) 5. (2)
6. (2) 7. (2) 8. (2) 9. (2) 10. (1)
"""
}
