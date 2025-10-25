package com.example.babydevelopmenttracker.model

data class BabyDevelopmentWeek(
    val week: Int,
    val babyHighlights: List<String>,
    val parentChanges: List<String>,
    val tips: List<String>
)

object BabyDevelopmentRepository {
    internal val DEFAULT_BABY_HIGHLIGHTS = listOf(
        "Baby is growing steadily with foundational organs developing.",
        "Tiny limbs, features, and systems continue forming each day."
    )
    internal val DEFAULT_PARENT_CHANGES = listOf(
        "Your body is adapting to pregnancy with hormonal shifts.",
        "Energy levels and emotions may fluctuate from day to day."
    )
    internal val DEFAULT_TIPS = listOf(
        "Continue prenatal care, balanced meals, and consistent rest.",
        "Reach out to your care team with any new or worsening symptoms."
    )

    val weeks: List<BabyDevelopmentWeek> = listOf(
        BabyDevelopmentWeek(
            week = 4,
            babyHighlights = listOf(
                "The implanted embryo begins building the amniotic sac for protection.",
                "Early placenta structures form pathways that will deliver oxygen and nutrients."
            ),
            parentChanges = listOf(
                "Light spotting or breast tenderness can appear as hormones surge.",
                "Heightened sense of smell and fatigue are common at this stage."
            ),
            tips = listOf(
                "Schedule an initial prenatal visit to review your health history.",
                "Begin prenatal vitamins with folic acid if you have not already.",
                "Review current medications or supplements with your provider."
            )
        ),
        BabyDevelopmentWeek(
            week = 5,
            babyHighlights = listOf(
                "The neural tube closes to kick-start formation of the brain and spinal cord.",
                "A primitive heart begins beating and pumping early blood cells."
            ),
            parentChanges = listOf(
                "Fatigue and mood swings often intensify as hCG levels rise quickly.",
                "Morning sickness or food aversions may make their first appearance."
            ),
            tips = listOf(
                "Keep easy, bland snacks on hand to settle your stomach.",
                "Sip water or electrolyte drinks throughout the day to stay hydrated.",
                "Create a symptom journal to discuss patterns with your care team."
            )
        ),
        BabyDevelopmentWeek(
            week = 6,
            babyHighlights = listOf(
                "Baby's heart rate climbs to roughly 100 beats per minute.",
                "Limb buds, early ear pits, and eye spots become more defined."
            ),
            parentChanges = listOf(
                "Nausea, bloating, and food aversions can intensify as progesterone rises.",
                "You may feel more tired than usual and crave extra sleep."
            ),
            tips = listOf(
                "Eat small, frequent meals to keep nausea manageable.",
                "Try ginger, vitamin B6, or acupressure bands for symptom relief.",
                "Prioritize rest when your body signals the need for sleep."
            )
        ),
        BabyDevelopmentWeek(
            week = 7,
            babyHighlights = listOf(
                "Facial features refine as the eyes, nose, and mouth take clearer shape.",
                "Tiny paddles at the ends of limbs hint at future fingers and toes."
            ),
            parentChanges = listOf(
                "Frequent urination and mild cramping may surface as the uterus expands.",
                "Vivid dreams or restless sleep can happen during hormonal changes."
            ),
            tips = listOf(
                "Stay hydrated during the day but limit evening fluids to reduce night trips.",
                "Practice calming bedtime rituals to wind down before sleep.",
                "Contact your provider if cramping becomes painful or is accompanied by bleeding."
            )
        ),
        BabyDevelopmentWeek(
            week = 8,
            babyHighlights = listOf(
                "Baby graduates from embryo to fetus with more recognizable features.",
                "Joints form at elbows and knees while the tail fully recedes."
            ),
            parentChanges = listOf(
                "Clothing may feel snug as your waistline thickens slightly.",
                "Increased saliva or a metallic taste can accompany hormone shifts."
            ),
            tips = listOf(
                "Build meals around protein-rich foods to stabilize energy.",
                "Explore prenatal stretching or gentle yoga for comfort.",
                "Ask about first trimester genetic screening options if interested."
            )
        ),
        BabyDevelopmentWeek(
            week = 9,
            babyHighlights = listOf(
                "The heart partitions into four chambers and beats strongly.",
                "Baby begins making spontaneous movements even though you cannot feel them yet."
            ),
            parentChanges = listOf(
                "Headaches may surface as blood volume and hormones continue rising.",
                "Acne flare-ups can occur due to oil production changes."
            ),
            tips = listOf(
                "Use gentle, pregnancy-safe skincare products for breakouts.",
                "Seek out dark, quiet spaces to rest when headaches strike.",
                "Keep prenatal appointments on schedule to monitor early development."
            )
        ),
        BabyDevelopmentWeek(
            week = 10,
            babyHighlights = listOf(
                "Vital organs are laid out and begin functioning in basic ways.",
                "Elbows bend and tiny nails start forming at the fingertips."
            ),
            parentChanges = listOf(
                "Waistline may continue to thicken even if weight gain is minimal.",
                "Nausea and fatigue slowly ease for some parents as hormones level."
            ),
            tips = listOf(
                "Balance meals with complex carbohydrates and lean protein.",
                "Explore safe, low-impact exercise like walking or swimming.",
                "Hydrate well to support increased blood volume."
            )
        ),
        BabyDevelopmentWeek(
            week = 11,
            babyHighlights = listOf(
                "The diaphragm develops, allowing baby to practice tiny hiccups.",
                "External genitalia begin differentiating and tooth buds keep growing."
            ),
            parentChanges = listOf(
                "You may notice more energy as early hormone surges stabilize.",
                "Skin often clears or glows thanks to improved circulation."
            ),
            tips = listOf(
                "Reintroduce gentle activity like short walks or prenatal yoga.",
                "Continue prenatal vitamins to support rapidly growing tissues.",
                "Schedule first trimester screening if recommended or desired."
            )
        ),
        BabyDevelopmentWeek(
            week = 12,
            babyHighlights = listOf(
                "Baby practices reflexes by opening and closing fingers and toes.",
                "Sucking motions begin as the nervous system refines connections."
            ),
            parentChanges = listOf(
                "The uterus rises above the pelvis, creating the start of a small bump.",
                "Digestive shifts can cause bloating or heartburn for some parents."
            ),
            tips = listOf(
                "Discuss screening results and any follow-up steps with your provider.",
                "Evaluate upcoming travel plans and confirm they are pregnancy-safe.",
                "Keep nutritious snacks on hand to steady blood sugar between meals."
            )
        ),
        BabyDevelopmentWeek(
            week = 13,
            babyHighlights = listOf(
                "Vocal cords form and intestines move fully into the abdomen.",
                "Baby begins producing urine that cycles into the amniotic fluid."
            ),
            parentChanges = listOf(
                "Second trimester energy often returns and appetite starts to rebound.",
                "Mood may lift as queasiness fades for many parents."
            ),
            tips = listOf(
                "Plan balanced meals rich in iron and calcium to fuel growth.",
                "Drink plenty of water to support increasing blood volume.",
                "Explore childbirth education resources or local class options."
            )
        ),
        BabyDevelopmentWeek(
            week = 14,
            babyHighlights = listOf(
                "Facial muscles enable tiny expressions such as squints or frowns.",
                "The spleen begins producing red blood cells and lanugo starts forming."
            ),
            parentChanges = listOf(
                "Relief from early nausea is common as the second trimester settles in.",
                "Nasal congestion can start because of increased blood flow to mucous membranes."
            ),
            tips = listOf(
                "Use a humidifier or saline spray to ease stuffiness.",
                "Keep tissues nearby and hydrate to thin mucus.",
                "Introduce gentle strength work to support posture and joints."
            )
        ),
        BabyDevelopmentWeek(
            week = 15,
            babyHighlights = listOf(
                "Bones harden and become visible on ultrasound.",
                "Baby can sense light filtering through closed eyelids."
            ),
            parentChanges = listOf(
                "Increased blood volume can cause stuffy noses or bleeding gums.",
                "You may notice a more defined bump as the uterus continues to grow."
            ),
            tips = listOf(
                "Schedule a dental checkup and keep up with oral hygiene.",
                "Use saline sprays or a humidifier for congestion relief.",
                "Elevate your head slightly during sleep to breathe easier."
            )
        ),
        BabyDevelopmentWeek(
            week = 16,
            babyHighlights = listOf(
                "The heart pumps around 25 quarts of blood every day.",
                "Muscles strengthen in preparation for the first detectable kicks."
            ),
            parentChanges = listOf(
                "Quickening—flutter-like sensations—may signal the first fetal movements.",
                "Balance and posture shift as your center of gravity changes."
            ),
            tips = listOf(
                "Note movement patterns in a journal to share at appointments.",
                "Stay hydrated and stretch hips and lower back regularly.",
                "Wear supportive footwear to reduce strain on joints."
            )
        ),
        BabyDevelopmentWeek(
            week = 17,
            babyHighlights = listOf(
                "The skeleton transitions from cartilage to stronger bone.",
                "Fat layers begin forming under the skin to regulate temperature."
            ),
            parentChanges = listOf(
                "Weight gain becomes steadier as baby and placenta grow.",
                "You might feel increased appetite and thirst throughout the day."
            ),
            tips = listOf(
                "Keep nutrient-dense snacks accessible for steady energy.",
                "Wear supportive footwear and consider a belly band for comfort.",
                "Maintain routine prenatal visits to monitor growth."
            )
        ),
        BabyDevelopmentWeek(
            week = 18,
            babyHighlights = listOf(
                "Ears reach their final position and baby can hear muffled sounds.",
                "Myelin begins coating nerves to improve brain communication."
            ),
            parentChanges = listOf(
                "Backaches or sciatic pain may appear as posture shifts.",
                "Ligaments stretch, causing occasional sharp twinges."
            ),
            tips = listOf(
                "Incorporate prenatal yoga or swimming for gentle support.",
                "Use supportive pillows when sitting or sleeping to ease strain.",
                "Alternate heat and rest to soothe sore muscles."
            )
        ),
        BabyDevelopmentWeek(
            week = 19,
            babyHighlights = listOf(
                "A protective vernix coating forms across baby's skin.",
                "Sensory development accelerates, enhancing touch and taste."
            ),
            parentChanges = listOf(
                "Round ligament pain can cause sharp, quick twinges.",
                "Skin may feel itchy as the belly stretches."
            ),
            tips = listOf(
                "Change positions slowly to reduce ligament strain.",
                "Try belly support bands if you need extra lift.",
                "Apply warm compresses or gentle massage for relief."
            )
        ),
        BabyDevelopmentWeek(
            week = 20,
            babyHighlights = listOf(
                "Baby develops recognizable wake and sleep cycles.",
                "The detailed anatomy scan offers a full survey of organs and limbs."
            ),
            parentChanges = listOf(
                "Consistent kicks and movement patterns become easier to feel.",
                "The top of the uterus reaches the level of your belly button."
            ),
            tips = listOf(
                "Review anatomy scan findings with your provider.",
                "Begin discussing birth preferences and support roles.",
                "Consider enrolling in a childbirth education class."
            )
        ),
        BabyDevelopmentWeek(
            week = 21,
            babyHighlights = listOf(
                "Baby practices swallowing amniotic fluid to build digestion skills.",
                "Tiny bowels start producing meconium, the first diaper contents."
            ),
            parentChanges = listOf(
                "Stretch marks or itchy skin can appear as the belly expands.",
                "You may feel more comfortable sleeping on your side."
            ),
            tips = listOf(
                "Moisturize daily to soothe stretching skin.",
                "Wear breathable fabrics and supportive bras for comfort.",
                "Hydrate to support skin elasticity and overall health."
            )
        ),
        BabyDevelopmentWeek(
            week = 22,
            babyHighlights = listOf(
                "Taste buds sharpen and baby reacts to flavors from your meals.",
                "Eyebrows and eyelashes become visible while senses refine."
            ),
            parentChanges = listOf(
                "Braxton Hicks contractions may start as the uterus rehearses for labor.",
                "Mild swelling in hands or feet can appear after long days."
            ),
            tips = listOf(
                "Time contractions and note any pattern changes.",
                "Rest with feet elevated to lessen swelling.",
                "Call your provider if tightenings become painful or regular."
            )
        ),
        BabyDevelopmentWeek(
            week = 23,
            babyHighlights = listOf(
                "Lungs produce surfactant so air sacs can stay open after birth.",
                "Rapid brain growth continues, adding folds and grooves."
            ),
            parentChanges = listOf(
                "Swelling in ankles or hands may increase, especially late in the day.",
                "The belly grows higher and may affect balance."
            ),
            tips = listOf(
                "Rotate ankles and stretch calves to ease swelling.",
                "Wear supportive shoes for long periods on your feet.",
                "Elevate legs whenever possible to improve circulation."
            )
        ),
        BabyDevelopmentWeek(
            week = 24,
            babyHighlights = listOf(
                "Skin becomes less translucent as fat accumulates underneath.",
                "Fingerprints and footprints develop unique patterns."
            ),
            parentChanges = listOf(
                "Glucose screening is often scheduled to check for gestational diabetes.",
                "You may notice more pronounced fetal movements throughout the day."
            ),
            tips = listOf(
                "Discuss glucose test preparation and timing with your provider.",
                "Review signs of preterm labor and when to seek care.",
                "Keep a running list of questions for upcoming visits."
            )
        ),
        BabyDevelopmentWeek(
            week = 25,
            babyHighlights = listOf(
                "Baby can sense touch and startle at loud noises outside the womb.",
                "Brown fat accumulates to provide warmth after birth."
            ),
            parentChanges = listOf(
                "Sleep may become harder as baby gets more active at night.",
                "Back discomfort can increase as your posture shifts."
            ),
            tips = listOf(
                "Adopt a calming bedtime routine and limit late screen time.",
                "Try side sleeping with supportive pillows around your body.",
                "Practice gentle stretching or massage before bed."
            )
        ),
        BabyDevelopmentWeek(
            week = 26,
            babyHighlights = listOf(
                "Eyelids open and close while lashes grow longer.",
                "Lungs rehearse breathing motions to prepare for the outside world."
            ),
            parentChanges = listOf(
                "Heartburn and indigestion can intensify as the uterus presses upward.",
                "You may feel more winded after climbing stairs or walking quickly."
            ),
            tips = listOf(
                "Eat smaller meals and avoid lying down immediately afterward.",
                "Ask about safe antacids if discomfort persists.",
                "Incorporate upright posture and slow breathing after meals."
            )
        ),
        BabyDevelopmentWeek(
            week = 27,
            babyHighlights = listOf(
                "Brain tissue expands quickly, adding more complex neural connections.",
                "Baby responds to your voice with increased movement or kicks."
            ),
            parentChanges = listOf(
                "Third trimester begins with more pronounced fatigue.",
                "Restless legs or nighttime cramps may disrupt sleep."
            ),
            tips = listOf(
                "Delegate tasks and build extra rest into your schedule.",
                "Stretch calves before bed and massage legs when restless.",
                "Focus on iron-rich meals to combat lingering fatigue."
            )
        ),
        BabyDevelopmentWeek(
            week = 28,
            babyHighlights = listOf(
                "Eyes detect light and REM sleep begins for the first time.",
                "Lungs continue practicing rhythmic breathing movements."
            ),
            parentChanges = listOf(
                "Backaches and leg cramps may worsen as belly size increases.",
                "Some parents notice more Braxton Hicks contractions."
            ),
            tips = listOf(
                "Consider a maternity support belt for extra back support.",
                "Schedule a third-trimester prenatal or breastfeeding class.",
                "Enjoy warm baths or showers to soothe sore muscles."
            )
        ),
        BabyDevelopmentWeek(
            week = 29,
            babyHighlights = listOf(
                "Muscles strengthen and coordination improves for smoother kicks.",
                "The brain forms grooves and folds to house expanding neurons."
            ),
            parentChanges = listOf(
                "Shortness of breath becomes more common as the uterus crowds the diaphragm.",
                "You may notice more pronounced swelling in feet or ankles."
            ),
            tips = listOf(
                "Take breaks when climbing stairs or walking long distances.",
                "Practice side-lying rest positions to open your airway.",
                "Prop yourself with pillows when sleeping to breathe easier."
            )
        ),
        BabyDevelopmentWeek(
            week = 30,
            babyHighlights = listOf(
                "Bone marrow takes over red blood cell production full-time.",
                "Lanugo starts thinning as baby accumulates insulating fat."
            ),
            parentChanges = listOf(
                "Colostrum may leak as breasts prepare for feeding.",
                "Balance can feel unsteady due to shifting weight distribution."
            ),
            tips = listOf(
                "Pack nursing pads if you notice leaking.",
                "Review infant feeding plans and support resources.",
                "Move slowly when changing positions to avoid dizziness."
            )
        ),
        BabyDevelopmentWeek(
            week = 31,
            babyHighlights = listOf(
                "The nervous system matures and senses sharpen further.",
                "Baby can turn toward familiar voices and sounds."
            ),
            parentChanges = listOf(
                "Braxton Hicks contractions can intensify, especially in the evening.",
                "Sleep disruptions are common due to frequent bathroom trips."
            ),
            tips = listOf(
                "Time any tightenings and report persistent patterns.",
                "Practice relaxation breathing or meditation before bed.",
                "Create a soothing wind-down ritual to encourage rest."
            )
        ),
        BabyDevelopmentWeek(
            week = 32,
            babyHighlights = listOf(
                "Baby gains roughly half a pound per week and practices breathing.",
                "Finger and toenails reach the tips of tiny digits."
            ),
            parentChanges = listOf(
                "Pelvic pressure increases as baby settles lower.",
                "Frequent bathroom trips may return with greater urgency."
            ),
            tips = listOf(
                "Do pelvic tilts or gentle stretches to ease lower back strain.",
                "Review your hospital bag checklist and start gathering essentials.",
                "Confirm childcare or pet-care plans for when labor begins."
            )
        ),
        BabyDevelopmentWeek(
            week = 33,
            babyHighlights = listOf(
                "Bones harden while the skull remains flexible for birth.",
                "Immune system development accelerates with antibody transfer."
            ),
            parentChanges = listOf(
                "Sleep becomes more challenging as belly size peaks.",
                "Swelling or vivid dreams may increase in late pregnancy."
            ),
            tips = listOf(
                "Elevate your feet whenever possible to reduce swelling.",
                "Use full-body pillows or wedges to support restful sleep.",
                "Keep water intake steady throughout the day."
            )
        ),
        BabyDevelopmentWeek(
            week = 34,
            babyHighlights = listOf(
                "Lungs reach maturity milestones to support breathing after birth.",
                "The central nervous system fine-tunes and pupils react to light."
            ),
            parentChanges = listOf(
                "Nesting energy might kick in, though energy levels can swing dramatically.",
                "Pelvic pressure and hip discomfort become more noticeable."
            ),
            tips = listOf(
                "Balance bursts of productivity with planned rest breaks.",
                "Finalize your birth plan and discuss preferences with your team.",
                "Install or inspect the car seat to ensure a secure fit."
            )
        ),
        BabyDevelopmentWeek(
            week = 35,
            babyHighlights = listOf(
                "Kidneys fully function and the liver processes waste efficiently.",
                "Limbs plump with soft fat to regulate temperature after birth."
            ),
            parentChanges = listOf(
                "Frequent bathroom trips continue as baby presses on the bladder.",
                "Pelvis may feel achy or loose due to hormone relaxin."
            ),
            tips = listOf(
                "Review newborn care basics with your support team.",
                "Confirm pediatrician appointments and insurance details.",
                "Keep healthy snacks within reach to fuel frequent hunger."
            )
        ),
        BabyDevelopmentWeek(
            week = 36,
            babyHighlights = listOf(
                "Baby practices sucking, gripping, and blinking for life outside the womb.",
                "Muscle tone improves as space becomes cozier."
            ),
            parentChanges = listOf(
                "Weekly prenatal visits often begin to monitor final preparations.",
                "A loosening mucus plug or increased discharge may appear."
            ),
            tips = listOf(
                "Confirm hospital preregistration and insurance paperwork.",
                "Rest between activities to conserve energy for labor.",
                "Keep your birth partner looped in on timing and needs."
            )
        ),
        BabyDevelopmentWeek(
            week = 37,
            babyHighlights = listOf(
                "Baby reaches early-term status with lungs and brain completing key milestones.",
                "Weight gain continues at roughly an ounce per day."
            ),
            parentChanges = listOf(
                "Pelvic pressure intensifies and irregular contractions are common.",
                "Sleep may be elusive as the body rehearses for labor."
            ),
            tips = listOf(
                "Keep your hospital bag accessible and ready to go.",
                "Refresh labor coping tools like breathing or massage techniques.",
                "Maintain hydration even if appetite dips."
            )
        ),
        BabyDevelopmentWeek(
            week = 38,
            babyHighlights = listOf(
                "Baby's grasp strengthens and lanugo mostly disappears.",
                "Fingernails can extend past fingertips, ready for trimming after birth."
            ),
            parentChanges = listOf(
                "Cervix begins ripening and you may notice increased discharge.",
                "Twinges of back pain or cramping can signal the body warming up."
            ),
            tips = listOf(
                "Stay flexible with plans and keep communicating with your provider.",
                "Prepare easy, nourishing meals or snacks for postpartum recovery.",
                "Continue gentle movement like walking to encourage circulation."
            )
        ),
        BabyDevelopmentWeek(
            week = 39,
            babyHighlights = listOf(
                "Baby fine-tunes breathing and sucking coordination for feeding.",
                "Fat stores keep building to support temperature control."
            ),
            parentChanges = listOf(
                "Energy surges can alternate with waves of intense fatigue.",
                "You may feel more frequent contractions or pelvic pressure."
            ),
            tips = listOf(
                "Practice relaxation or meditation to stay centered.",
                "Coordinate postpartum support and visiting plans.",
                "Verify childcare or pet care logistics for your time at the hospital."
            )
        ),
        BabyDevelopmentWeek(
            week = 40,
            babyHighlights = listOf(
                "Organs are ready for life outside and vernix remains mainly in skin creases.",
                "Bones stay flexible to navigate the birth canal safely."
            ),
            parentChanges = listOf(
                "Strong contractions and increased pressure signal due date week.",
                "More frequent cervical checks may happen to assess dilation."
            ),
            tips = listOf(
                "Discuss induction plans or expectant management with your provider.",
                "Keep nourishing snacks and hydration nearby between contractions.",
                "Rest whenever possible to conserve energy for labor."
            )
        ),
        BabyDevelopmentWeek(
            week = 41,
            babyHighlights = listOf(
                "Baby continues to grow while amniotic fluid levels are monitored closely.",
                "Placenta efficiency is evaluated to ensure ongoing support."
            ),
            parentChanges = listOf(
                "Membrane sweeps or non-stress tests may begin at your visits.",
                "You might experience more intense practice contractions."
            ),
            tips = listOf(
                "Attend all monitoring appointments to track baby’s well-being.",
                "Stay hydrated and nourished while you wait for labor to begin.",
                "Keep your hospital bag ready and transportation plan confirmed."
            )
        ),
        BabyDevelopmentWeek(
            week = 42,
            babyHighlights = listOf(
                "Post-term babies often have longer nails and may shed peeling skin.",
                "They continue practicing breathing while awaiting induction or labor."
            ),
            parentChanges = listOf(
                "Providers typically schedule induction if labor hasn't started soon.",
                "Emotions can run high as anticipation stretches past the due date."
            ),
            tips = listOf(
                "Review induction methods and what to expect during each step.",
                "Confirm childcare coverage and postpartum support in advance.",
                "Pack any last-minute hospital essentials you may have overlooked."
            )
        )
    ).associateBy { it.week }
        .let { map ->
            (4..42).map { week ->
                map[week] ?: BabyDevelopmentWeek(
                    week = week,
                    babyHighlights = DEFAULT_BABY_HIGHLIGHTS,
                    parentChanges = DEFAULT_PARENT_CHANGES,
                    tips = DEFAULT_TIPS
                )
            }
        }

    private val trimesterChecklist = mapOf(
        1 to listOf(
            "Review prenatal vitamins and any supplements with your care team.",
            "Note questions about early symptoms to discuss at your next visit.",
            "Schedule routine bloodwork or screening recommended for this trimester."
        ),
        2 to listOf(
            "Check in on anatomy scan scheduling and insurance coverage.",
            "Track weight, blood pressure, and any new symptoms between visits.",
            "Confirm childbirth education or hospital orientation dates."
        ),
        3 to listOf(
            "Review birth preferences and discuss them with your provider.",
            "Finalize pediatrician selection and newborn care questions.",
            "Monitor fetal movement patterns and call if anything feels off."
        )
    )

    private val specificChecklist = mapOf(
        12 to listOf(
            "Complete first trimester screening follow-ups if ordered.",
            "Plan your next prenatal appointment around week 16."
        ),
        20 to listOf(
            "Attend the detailed anatomy ultrasound if it is scheduled this week.",
            "Discuss any travel plans with your provider before booking."
        ),
        28 to listOf(
            "Complete glucose screening and review third trimester lab work.",
            "Ask about Tdap and flu vaccines if you have not received them yet."
        ),
        36 to listOf(
            "Confirm group B strep testing and understand next steps.",
            "Review signs of labor and when to head to your birth location."
        ),
        40 to listOf(
            "Discuss post-dates monitoring plans and induction options.",
            "Ask how often to come in for non-stress tests if still pregnant."
        )
    )

    private val trimesterSupportIdeas = mapOf(
        1 to listOf(
            "Prep easy snacks, ginger tea, or lemon water to help with nausea.",
            "Offer to handle chores when fatigue peaks in the evenings.",
            "Join prenatal appointments to hear guidance first-hand if invited."
        ),
        2 to listOf(
            "Plan a relaxing outing that works with renewed energy levels.",
            "Help track appointment dates and organize shared calendars.",
            "Check in about how to support changing body comfort needs."
        ),
        3 to listOf(
            "Create a calm space at home for rest and late-pregnancy routines.",
            "Stay on top of nursery or supply prep without pressuring timelines.",
            "Practice labor comfort techniques together from any birth classes."
        )
    )

    private val specificSupportIdeas = mapOf(
        14 to listOf(
            "Celebrate entering the second trimester with a favorite meal or walk."
        ),
        24 to listOf(
            "Help research childcare or parental leave logistics together."
        ),
        30 to listOf(
            "Assemble a go-bag checklist and start packing shared essentials."
        ),
        38 to listOf(
            "Keep the car fueled and routes planned for the hospital or birthing center."
        ),
        41 to listOf(
            "Encourage rest and offer reassurance while waiting for labor to begin."
        )
    )

    fun doctorChecklistForWeek(week: Int): List<String> {
        val trimester = when (week) {
            in 4..13 -> 1
            in 14..27 -> 2
            in 28..42 -> 3
            else -> null
        }

        val base = trimester?.let { trimesterChecklist[it] }.orEmpty()
        val specific = specificChecklist[week].orEmpty()

        return (specific + base).ifEmpty { DEFAULT_TIPS }
    }

    fun partnerSupportForWeek(week: Int): List<String> {
        val trimester = when (week) {
            in 4..13 -> 1
            in 14..27 -> 2
            in 28..42 -> 3
            else -> null
        }

        val base = trimester?.let { trimesterSupportIdeas[it] }.orEmpty()
        val specific = specificSupportIdeas[week].orEmpty()

        return (specific + base).ifEmpty {
            listOf(
                "Check in with encouraging messages and see what support would feel best today.",
                "Celebrate the milestones already reached together."
            )
        }
    }
}

fun BabyDevelopmentRepository.findWeek(week: Int): BabyDevelopmentWeek? =
    weeks.find { it.week == week }
