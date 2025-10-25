package com.example.babydevelopmenttracker.model

data class BabyDevelopmentWeek(
    val week: Int,
    val babyHighlights: String,
    val parentChanges: String,
    val tips: String
)

object BabyDevelopmentRepository {
    internal const val DEFAULT_BABY_HIGHLIGHTS =
        "Baby is growing steadily with foundational organs developing."
    internal const val DEFAULT_PARENT_CHANGES =
        "Your body is adapting to pregnancy with hormonal shifts."
    internal const val DEFAULT_TIPS =
        "Continue prenatal care, balanced meals, and consistent rest."

    val weeks: List<BabyDevelopmentWeek> = listOf(
        BabyDevelopmentWeek(
            week = 4,
            babyHighlights = "Implanted embryo builds the amniotic sac while the placenta's early structures begin forming.",
            parentChanges = "Light spotting, breast tenderness, and heightened smell are common as hormones surge.",
            tips = "Schedule an initial prenatal visit, begin prenatal vitamins with folic acid, and review medications with your provider."
        ),
        BabyDevelopmentWeek(
            week = 5,
            babyHighlights = "Neural tube closes and the beginnings of the heart, brain, and spinal cord take shape.",
            parentChanges = "Fatigue and mood swings often intensify, and morning sickness may appear.",
            tips = "Keep easy snacks nearby, sip water throughout the day, and create a symptom journal for appointments."
        ),
        BabyDevelopmentWeek(
            week = 6,
            babyHighlights = "The heart beats about 100 times per minute while limb buds, ear pits, and eye spots emerge.",
            parentChanges = "Nausea, food aversions, and bloating can increase as progesterone rises.",
            tips = "Eat small, frequent meals, try ginger or vitamin B6 for nausea relief, and rest whenever possible."
        ),
        BabyDevelopmentWeek(
            week = 7,
            babyHighlights = "Facial features refine, the brain rapidly produces neurons, and tiny paddles hint at fingers and toes.",
            parentChanges = "Frequent urination, mild cramping, and vivid dreams may surface.",
            tips = "Stay hydrated, limit evening fluids to manage bathroom trips, and practice calming bedtime routines."
        ),
        BabyDevelopmentWeek(
            week = 8,
            babyHighlights = "Baby graduates from embryo to fetus as joints form and the tail fully recedes.",
            parentChanges = "Clothing may feel snug and you might notice increased saliva or metallic taste.",
            tips = "Focus on protein-rich meals, explore prenatal stretching, and ask about genetic screening options."
        ),
        BabyDevelopmentWeek(
            week = 9,
            babyHighlights = "Heart partitions into four chambers, basic tooth buds appear, and baby starts making spontaneous movements.",
            parentChanges = "Headaches and acne flare-ups can happen due to hormonal shifts.",
            tips = "Use gentle skincare products, prioritize dark, quiet spaces for headaches, and keep prenatal appointments on track."
        ),
        BabyDevelopmentWeek(
            week = 10,
            babyHighlights = "Vital organs are laid out, elbows bend, and tiny nails start forming at the fingertips.",
            parentChanges = "Waistline may thicken while nausea and fatigue slowly ease for some.",
            tips = "Balance meals with complex carbs and lean protein, and explore safe low-impact exercise options."
        ),
        BabyDevelopmentWeek(
            week = 11,
            babyHighlights = "Diaphragm development supports hiccups, external genitalia begin to differentiate, and tooth buds grow.",
            parentChanges = "You may feel more energetic and notice clearer skin as hormones rebalance.",
            tips = "Reintroduce gentle activity like walking, continue prenatal vitamins, and schedule first trimester screening if desired."
        ),
        BabyDevelopmentWeek(
            week = 12,
            babyHighlights = "Baby practices reflexes—opening and closing fingers, curling toes, and making sucking motions.",
            parentChanges = "The uterus rises above the pelvis creating the start of a visible bump.",
            tips = "Discuss any screening results, evaluate travel plans with your provider, and keep nutritious snacks handy."
        ),
        BabyDevelopmentWeek(
            week = 13,
            babyHighlights = "Vocal cords form, intestines move into the abdomen, and baby begins producing urine that flows into amniotic fluid.",
            parentChanges = "Second trimester energy returns for many, and appetite begins to rebound.",
            tips = "Plan balanced meals rich in iron and calcium, drink plenty of water, and explore childbirth education options."
        ),
        BabyDevelopmentWeek(
            week = 14,
            babyHighlights = "Facial muscles enable expressions, the spleen makes red blood cells, and lanugo begins covering the skin.",
            parentChanges = "You may feel relief from early nausea while nasal congestion can start.",
            tips = "Use a humidifier, keep tissues nearby, and introduce gentle strength work to support posture."
        ),
        BabyDevelopmentWeek(
            week = 15,
            babyHighlights = "Bones harden, ears move into position, and baby can sense light through closed eyelids.",
            parentChanges = "Your body pumps more blood, which can cause stuffy nose or bleeding gums.",
            tips = "Schedule a dental checkup, use saline spray for congestion, and elevate your head during sleep."
        ),
        BabyDevelopmentWeek(
            week = 16,
            babyHighlights = "The heart pumps about 25 quarts of blood daily and muscles strengthen for the first detectable kicks soon.",
            parentChanges = "Some experience quickening—flutter-like sensations that signal fetal movement.",
            tips = "Note movement patterns in a journal, hydrate well, and stretch hips and lower back regularly."
        ),
        BabyDevelopmentWeek(
            week = 17,
            babyHighlights = "Skeleton transforms from rubbery cartilage to bone, and fat layers begin forming under the skin.",
            parentChanges = "Weight gain becomes steadier and you might feel increased appetite.",
            tips = "Keep nutrient-dense snacks available, wear supportive footwear, and maintain routine prenatal visits."
        ),
        BabyDevelopmentWeek(
            week = 18,
            babyHighlights = "Ears reach their final position, myelin starts coating nerves, and baby can hear muffled sounds and your heartbeat.",
            parentChanges = "Backaches or sciatica may appear as posture shifts and ligaments stretch.",
            tips = "Incorporate prenatal yoga or swimming, and use supportive pillows when sitting or sleeping."
        ),
        BabyDevelopmentWeek(
            week = 19,
            babyHighlights = "A cheesy protective layer called vernix forms on baby's skin while sensory development accelerates.",
            parentChanges = "Round ligament pain can cause sharp twinges as your uterus grows.",
            tips = "Change positions slowly, try belly support bands, and use warm compresses for relief."
        ),
        BabyDevelopmentWeek(
            week = 20,
            babyHighlights = "Halfway there! Baby develops wake-sleep cycles and the anatomy scan provides detailed views of organs and limbs.",
            parentChanges = "Consistent kicks are common and the fundus reaches your belly button.",
            tips = "Review anatomy scan findings, start discussing birth preferences, and consider a childbirth education class."
        ),
        BabyDevelopmentWeek(
            week = 21,
            babyHighlights = "Digestive system practices swallowing amniotic fluid, producing tiny meconium in the bowels.",
            parentChanges = "Skin stretching can cause itchiness or new stretch marks.",
            tips = "Moisturize daily, wear breathable fabrics, and hydrate to support skin elasticity."
        ),
        BabyDevelopmentWeek(
            week = 22,
            babyHighlights = "Taste buds sharpen, eyebrows and eyelashes become visible, and baby responds to rhythmic sounds.",
            parentChanges = "Braxton Hicks contractions may start as your uterus rehearses for labor.",
            tips = "Time contractions, rest with your feet elevated, and call your provider if they become painful or regular."
        ),
        BabyDevelopmentWeek(
            week = 23,
            babyHighlights = "Lungs produce surfactant, enabling air sacs to stay open after birth, and rapid brain growth continues.",
            parentChanges = "Swelling in ankles or hands may appear, especially late in the day.",
            tips = "Rotate ankles, wear supportive shoes, and elevate legs whenever you can."
        ),
        BabyDevelopmentWeek(
            week = 24,
            babyHighlights = "Skin becomes less translucent, fingerprints form, and your baby reaches a viability milestone with specialized care.",
            parentChanges = "Glucose screening is often scheduled soon to check for gestational diabetes.",
            tips = "Discuss the glucose test prep, review signs of preterm labor, and keep a list of questions for your next visit."
        ),
        BabyDevelopmentWeek(
            week = 25,
            babyHighlights = "Baby can sense touch, startle at loud noises, and build brown fat for warmth after birth.",
            parentChanges = "Sleep may become harder as baby gets more active and your belly grows.",
            tips = "Adopt a calming bedtime routine, try side sleeping with pillows, and reduce screen time before bed."
        ),
        BabyDevelopmentWeek(
            week = 26,
            babyHighlights = "Eyelids open and close, lashes grow in, and lungs rehearse breathing motions.",
            parentChanges = "Heartburn and indigestion can intensify as the uterus presses on the stomach.",
            tips = "Eat small meals, avoid lying down after eating, and ask about safe antacids if discomfort persists."
        ),
        BabyDevelopmentWeek(
            week = 27,
            babyHighlights = "Brain tissue expands, taste buds mature, and baby responds to your voice with increased movement.",
            parentChanges = "Third trimester begins with more pronounced fatigue and possible restless legs.",
            tips = "Delegate tasks, stretch calves before bed, and prioritize iron-rich meals to combat fatigue."
        ),
        BabyDevelopmentWeek(
            week = 28,
            babyHighlights = "Eyes can detect light, REM sleep begins, and lungs continue practicing rhythmic breathing.",
            parentChanges = "Backaches and leg cramps may worsen while belly size increases noticeably.",
            tips = "Consider a maternity support belt, schedule a third-trimester prenatal class, and enjoy warm baths for sore muscles."
        ),
        BabyDevelopmentWeek(
            week = 29,
            babyHighlights = "Muscles strengthen, the brain forms grooves and folds, and baby’s head grows to accommodate expanding neurons.",
            parentChanges = "Shortness of breath becomes more common as the uterus crowds the diaphragm.",
            tips = "Take breaks when climbing stairs, practice side-lying rest, and prop yourself up with pillows when sleeping."
        ),
        BabyDevelopmentWeek(
            week = 30,
            babyHighlights = "Bone marrow takes over red blood cell production and lanugo starts thinning as baby accumulates fat.",
            parentChanges = "Colostrum may leak as breasts prepare for feeding and balance can feel unsteady.",
            tips = "Pack nursing pads, review infant feeding plans, and move slowly when changing positions."
        ),
        BabyDevelopmentWeek(
            week = 31,
            babyHighlights = "Nervous system matures, five senses sharpen, and baby can turn toward familiar voices.",
            parentChanges = "Braxton Hicks contractions can intensify and sleep disruptions are common.",
            tips = "Time any contractions, practice relaxation breathing, and create a soothing pre-sleep wind-down ritual."
        ),
        BabyDevelopmentWeek(
            week = 32,
            babyHighlights = "Baby gains about half a pound per week, practices breathing and sucking, and nails reach fingertips.",
            parentChanges = "Pelvic pressure and frequent bathroom trips may increase as baby settles lower.",
            tips = "Do pelvic tilts, review your hospital bag checklist, and confirm childcare or pet-care plans for labor."
        ),
        BabyDevelopmentWeek(
            week = 33,
            babyHighlights = "Bones harden while the skull stays flexible, and immune system development accelerates.",
            parentChanges = "Sleep can be challenging, swelling may increase, and vivid dreams are common.",
            tips = "Elevate your feet, use full-body pillows, and keep water intake steady throughout the day."
        ),
        BabyDevelopmentWeek(
            week = 34,
            babyHighlights = "Lungs reach maturity milestones, the central nervous system fine-tunes, and pupils react to light.",
            parentChanges = "Nesting energy might kick in, though energy levels can swing dramatically.",
            tips = "Balance bursts of productivity with planned rest, finalize your birth plan, and install the car seat."
        ),
        BabyDevelopmentWeek(
            week = 35,
            babyHighlights = "Kidneys fully function, liver processes waste, and limbs plump with soft fat.",
            parentChanges = "Frequent bathroom trips continue as baby presses on the bladder and pelvis.",
            tips = "Review newborn care basics, confirm pediatrician appointments, and keep healthy snacks within reach."
        ),
        BabyDevelopmentWeek(
            week = 36,
            babyHighlights = "Baby practices sucking, gripping, and blinking—key skills for life outside the womb.",
            parentChanges = "Weekly prenatal visits begin, mucus plug may loosen, and walking can feel wobbly.",
            tips = "Confirm hospital preregistration, rest between activities, and keep your birth partner looped in."
        ),
        BabyDevelopmentWeek(
            week = 37,
            babyHighlights = "Considered early term; lungs and brain complete crucial final maturation and baby gains about an ounce daily.",
            parentChanges = "Pelvic pressure intensifies, irregular contractions are common, and sleep may be elusive.",
            tips = "Keep your hospital bag accessible, refresh labor coping tools, and maintain hydration even when appetite dips."
        ),
        BabyDevelopmentWeek(
            week = 38,
            babyHighlights = "Baby's grasp strengthens, lanugo mostly disappears, and fingernails may extend past fingertips.",
            parentChanges = "Cervix begins ripening and you might experience increased discharge or mucus plug fragments.",
            tips = "Stay flexible with plans, keep nourishing meals easy to access, and continue gentle movement like walking."
        ),
        BabyDevelopmentWeek(
            week = 39,
            babyHighlights = "Baby fine-tunes breathing and sucking coordination while continuing to build fat for temperature control.",
            parentChanges = "Energy surges and restlessness can alternate with waves of fatigue.",
            tips = "Practice relaxation or meditation, coordinate postpartum support, and verify childcare or pet care logistics."
        ),
        BabyDevelopmentWeek(
            week = 40,
            babyHighlights = "Due date week! Organs are ready for life outside, vernix remains only in creases, and bones stay flexible for birth.",
            parentChanges = "You may feel strong contractions, increased pressure, and more frequent cervical checks.",
            tips = "Discuss induction plans or expectant management, keep nourishing snacks handy, and rest whenever contractions allow."
        ),
        BabyDevelopmentWeek(
            week = 41,
            babyHighlights = "Baby continues to grow, amniotic fluid levels are monitored closely, and placenta efficiency is evaluated.",
            parentChanges = "Membrane sweeps or NST monitoring may begin as providers assess placenta health.",
            tips = "Attend all monitoring appointments, stay hydrated, and keep your hospital bag ready by the door."
        ),
        BabyDevelopmentWeek(
            week = 42,
            babyHighlights = "Post-term babies have longer nails, less vernix, and may show peeling skin as they outgrow the womb.",
            parentChanges = "Providers typically schedule induction if labor hasn't started to keep baby safe.",
            tips = "Review induction methods, confirm childcare coverage, and pack any last-minute hospital essentials."
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
}

fun BabyDevelopmentRepository.findWeek(week: Int): BabyDevelopmentWeek? =
    weeks.find { it.week == week }
