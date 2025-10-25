package com.example.babydevelopmenttracker.model

data class BabyDevelopmentWeek(
    val week: Int,
    val babyHighlights: String,
    val parentChanges: String,
    val tips: String
)

object BabyDevelopmentRepository {
    val weeks: List<BabyDevelopmentWeek> = listOf(
        BabyDevelopmentWeek(
            week = 4,
            babyHighlights = "Fertilized egg implants and cells begin forming the placenta and embryo.",
            parentChanges = "You may notice light spotting and heightened sense of smell.",
            tips = "Start prenatal vitamins if you haven't already and schedule a prenatal appointment."
        ),
        BabyDevelopmentWeek(
            week = 5,
            babyHighlights = "Neural tube closes and the heart starts forming its chambers.",
            parentChanges = "Fatigue and tender breasts are common as hormones rise.",
            tips = "Establish a consistent sleep schedule and track any symptoms for your provider."
        ),
        BabyDevelopmentWeek(
            week = 6,
            babyHighlights = "Heart is beating and limb buds appear for future arms and legs.",
            parentChanges = "Nausea may intensify and mood swings can appear.",
            tips = "Keep snacks nearby to manage nausea and stay hydrated with small sips of water."
        ),
        BabyDevelopmentWeek(
            week = 7,
            babyHighlights = "Facial features begin taking shape including nostrils and eye lenses.",
            parentChanges = "Frequent urination and food aversions might increase.",
            tips = "Plan small, balanced meals and explore prenatal yoga or stretching."
        ),
        BabyDevelopmentWeek(
            week = 8,
            babyHighlights = "Baby's tail disappears and fingers and toes start separating.",
            parentChanges = "Clothes might feel snug as the uterus grows slightly.",
            tips = "Focus on nutrient-rich foods and discuss safe exercise with your provider."
        ),
        BabyDevelopmentWeek(
            week = 9,
            babyHighlights = "Major organs continue to develop rapidly and baby begins moving.",
            parentChanges = "Hormones can lead to headaches or acne flare-ups.",
            tips = "Maintain gentle skincare routines and rest in dark rooms if headaches appear."
        ),
        BabyDevelopmentWeek(
            week = 10,
            babyHighlights = "Vital organs are fully formed and the embryo is now called a fetus.",
            parentChanges = "Waistline may thicken and morning sickness may persist.",
            tips = "Carry small meals for on-the-go nourishment and keep prenatal appointments."
        ),
        BabyDevelopmentWeek(
            week = 11,
            babyHighlights = "Baby's diaphragm is developing and tiny fingernails form.",
            parentChanges = "Energy may begin returning and nausea often eases.",
            tips = "Introduce light activity like walking and continue prenatal vitamins."
        ),
        BabyDevelopmentWeek(
            week = 12,
            babyHighlights = "Reflexes develop, allowing baby to open and close fingers.",
            parentChanges = "You might notice a small bump as the uterus rises.",
            tips = "Schedule the first trimester screening if recommended by your provider."
        ),
        BabyDevelopmentWeek(
            week = 13,
            babyHighlights = "Vocal cords form and baby starts producing urine.",
            parentChanges = "Second trimester brings renewed energy and reduced nausea.",
            tips = "Plan balanced meals rich in protein and iron to support rapid growth."
        ),
        BabyDevelopmentWeek(
            week = 14,
            babyHighlights = "Facial muscles allow baby to make expressions like squinting.",
            parentChanges = "You may feel relief from first trimester fatigue.",
            tips = "Begin thinking about childbirth education classes and movement routines."
        ),
        BabyDevelopmentWeek(
            week = 15,
            babyHighlights = "Bones harden and baby can sense light through closed eyelids.",
            parentChanges = "Increased blood volume may lead to stuffy nose or bleeding gums.",
            tips = "Use a humidifier for congestion and maintain regular dental care."
        ),
        BabyDevelopmentWeek(
            week = 16,
            babyHighlights = "Baby's ears move into position and muscles strengthen for movement.",
            parentChanges = "Some begin to feel the first flutters known as quickening.",
            tips = "Practice mindful breathing and note movement patterns in a journal."
        ),
        BabyDevelopmentWeek(
            week = 17,
            babyHighlights = "Skeleton transitions from cartilage to bone and sweat glands form.",
            parentChanges = "Appetite grows and weight gain becomes steadier.",
            tips = "Stock up on healthy snacks and stay consistent with prenatal check-ins."
        ),
        BabyDevelopmentWeek(
            week = 18,
            babyHighlights = "Ears are fully formed and baby can hear muffled sounds.",
            parentChanges = "Backaches may appear as posture shifts.",
            tips = "Add prenatal stretching and supportive pillows for sleep."
        ),
        BabyDevelopmentWeek(
            week = 19,
            babyHighlights = "Vernix caseosa begins coating baby's skin for protection.",
            parentChanges = "You may notice round ligament pain from the stretching uterus.",
            tips = "Change positions slowly and use warm compresses for discomfort."
        ),
        BabyDevelopmentWeek(
            week = 20,
            babyHighlights = "Halfway point! Baby develops sleep and wake cycles.",
            parentChanges = "Anatomy scan typically occurs; many feel consistent kicks.",
            tips = "Discuss the anatomy ultrasound findings and review birth preferences."
        ),
        BabyDevelopmentWeek(
            week = 21,
            babyHighlights = "Digestive system practices swallowing amniotic fluid.",
            parentChanges = "Skin may appear itchy as it stretches.",
            tips = "Moisturize daily and stay hydrated to ease skin tightness."
        ),
        BabyDevelopmentWeek(
            week = 22,
            babyHighlights = "Baby's senses sharpen and taste buds develop.",
            parentChanges = "Braxton Hicks contractions might start.",
            tips = "Track contractions and rest with feet elevated when they occur."
        ),
        BabyDevelopmentWeek(
            week = 23,
            babyHighlights = "Lungs produce surfactant preparing for breathing.",
            parentChanges = "Swelling in ankles or feet can appear.",
            tips = "Rotate ankles gently and wear supportive footwear."
        ),
        BabyDevelopmentWeek(
            week = 24,
            babyHighlights = "Skin becomes less translucent and facial features are distinct.",
            parentChanges = "Viability milestone; glucose screening often scheduled soon.",
            tips = "Plan the glucose test and discuss preterm labor signs with your provider."
        ),
        BabyDevelopmentWeek(
            week = 25,
            babyHighlights = "Baby responds to touch and starts building fat stores.",
            parentChanges = "Sleep may be harder as baby grows more active.",
            tips = "Establish a calming bedtime routine and try side-sleeping with pillows."
        ),
        BabyDevelopmentWeek(
            week = 26,
            babyHighlights = "Eyelids open and close, and lungs continue maturing.",
            parentChanges = "Heartburn can intensify as the uterus presses on the stomach.",
            tips = "Eat small meals and avoid lying down immediately after eating."
        ),
        BabyDevelopmentWeek(
            week = 27,
            babyHighlights = "Brain tissue is expanding rapidly for cognition.",
            parentChanges = "Third trimester begins with more pronounced fatigue.",
            tips = "Delegate tasks, accept help, and focus on nutrient-dense meals."
        ),
        BabyDevelopmentWeek(
            week = 28,
            babyHighlights = "Eyes can detect light and baby practices breathing motions.",
            parentChanges = "Backaches and leg cramps may worsen.",
            tips = "Incorporate prenatal massages or warm baths to relax muscles."
        ),
        BabyDevelopmentWeek(
            week = 29,
            babyHighlights = "Muscles and lungs continue developing while head grows to house the brain.",
            parentChanges = "Shortness of breath becomes more common as uterus expands.",
            tips = "Take breaks when climbing stairs and sleep propped with extra pillows."
        ),
        BabyDevelopmentWeek(
            week = 30,
            babyHighlights = "Baby's bone marrow takes over red blood cell production.",
            parentChanges = "Colostrum may leak as breasts prepare for feeding.",
            tips = "Pack nursing pads and discuss feeding preferences with support partners."
        ),
        BabyDevelopmentWeek(
            week = 31,
            babyHighlights = "Nervous system matures and baby can regulate body temperature better.",
            parentChanges = "Braxton Hicks contractions can intensify.",
            tips = "Time contractions and call your provider if they become regular."
        ),
        BabyDevelopmentWeek(
            week = 32,
            babyHighlights = "Baby gains weight rapidly and practices breathing rhythmic movements.",
            parentChanges = "Pelvic pressure may increase as baby settles lower.",
            tips = "Do pelvic tilts and ensure your hospital bag checklist is underway."
        ),
        BabyDevelopmentWeek(
            week = 33,
            babyHighlights = "Bones harden though skull remains soft for birth.",
            parentChanges = "Sleep can be challenging and swelling may increase.",
            tips = "Elevate feet, use body pillows, and keep water intake steady."
        ),
        BabyDevelopmentWeek(
            week = 34,
            babyHighlights = "Lungs finish maturing and baby practices opening eyes during wakeful periods.",
            parentChanges = "Nesting instinct might kick in and energy can fluctuate.",
            tips = "Prioritize rest between bursts of energy and finalize birth plan details."
        ),
        BabyDevelopmentWeek(
            week = 35,
            babyHighlights = "Kidneys fully developed and liver processes waste.",
            parentChanges = "Frequent bathroom trips continue as baby settles lower.",
            tips = "Review newborn care basics and install the car seat for safety checks."
        ),
        BabyDevelopmentWeek(
            week = 36,
            babyHighlights = "Baby practices sucking and gripping, preparing for life outside.",
            parentChanges = "Weekly prenatal visits begin and mucus plug may release.",
            tips = "Confirm hospital preregistration and rest when possible."
        ),
        BabyDevelopmentWeek(
            week = 37,
            babyHighlights = "Considered early term; lungs and brain continue final maturation.",
            parentChanges = "You may feel strong pelvic pressure and irregular contractions.",
            tips = "Keep hospital bag accessible and review labor coping techniques."
        ),
        BabyDevelopmentWeek(
            week = 38,
            babyHighlights = "Baby's grasp strengthens and lanugo mostly disappears.",
            parentChanges = "Cervix begins to ripen and you may feel more discharge.",
            tips = "Stay flexible with plans and maintain nourishing meals and hydration."
        ),
        BabyDevelopmentWeek(
            week = 39,
            babyHighlights = "Baby continues building fat and practicing breathing and blinking.",
            parentChanges = "Energy surges and restlessness can alternate.",
            tips = "Practice relaxation techniques and coordinate postpartum support."
        ),
        BabyDevelopmentWeek(
            week = 40,
            babyHighlights = "Due date! Baby's organs are ready for life outside the womb.",
            parentChanges = "You might feel increased contractions and pressure.",
            tips = "Check in with your provider about induction plans and self-care."
        ),
        BabyDevelopmentWeek(
            week = 41,
            babyHighlights = "Baby continues to grow and amniotic fluid levels are monitored closely.",
            parentChanges = "Membrane sweep or induction discussions may begin.",
            tips = "Attend all monitoring appointments and keep communicating with your provider."
        ),
        BabyDevelopmentWeek(
            week = 42,
            babyHighlights = "Post-term babies have longer nails and less vernix.",
            parentChanges = "Providers typically schedule induction if labor hasn't begun.",
            tips = "Review induction methods and pack any last-minute hospital essentials."
        )
    ).associateBy { it.week }
        .let { map ->
            (4..42).map { week ->
                map[week] ?: BabyDevelopmentWeek(
                    week = week,
                    babyHighlights = "Baby is growing steadily with foundational organs developing.",
                    parentChanges = "Your body is adapting to pregnancy with hormonal shifts.",
                    tips = "Continue prenatal care, balanced meals, and consistent rest."
                )
            }
        }
}

fun BabyDevelopmentRepository.findWeek(week: Int): BabyDevelopmentWeek? =
    weeks.find { it.week == week }
