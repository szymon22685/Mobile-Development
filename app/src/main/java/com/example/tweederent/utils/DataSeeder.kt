package com.example.tweederent.utils

import com.example.tweederent.data.Category
import com.example.tweederent.data.Device
import com.example.tweederent.data.Location
import com.example.tweederent.data.Review
import com.example.tweederent.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Locale
import java.util.Random
import java.util.UUID

class DataSeeder {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val flemishLocations = listOf(
        Location( // Antwerp Central
            address = "Koningin Astridplein 27",
            latitude = 51.2172,
            longitude = 4.4210,
            city = "Antwerp",
            postalCode = "2018"
        ),
        Location( // Berchem
            address = "Driekoningenstraat 126",
            latitude = 51.2008,
            longitude = 4.4324,
            city = "Berchem",
            postalCode = "2600"
        ),
        Location( // Deurne
            address = "Frank Craeybeckxlaan 22",
            latitude = 51.2120,
            longitude = 4.4696,
            city = "Deurne",
            postalCode = "2100"
        ),
        Location( // Wilrijk
            address = "Bist 1",
            latitude = 51.1711,
            longitude = 4.3967,
            city = "Wilrijk",
            postalCode = "2610"
        ),
        Location( // Merksem
            address = "Bredabaan 382",
            latitude = 51.2372,
            longitude = 4.4428,
            city = "Merksem",
            postalCode = "2170"
        ),
        Location( // Hoboken
            address = "Kioskplaats 54",
            latitude = 51.1786,
            longitude = 4.3648,
            city = "Hoboken",
            postalCode = "2660"
        ),
        Location( // Mortsel
            address = "Gemeenteplein 1",
            latitude = 51.1717,
            longitude = 4.4556,
            city = "Mortsel",
            postalCode = "2640"
        ),
        Location( // Mechelen
            address = "Grote Markt 21",
            latitude = 51.0279,
            longitude = 4.4803,
            city = "Mechelen",
            postalCode = "2800"
        ),
        Location( // Lier
            address = "Grote Markt 58",
            latitude = 51.1303,
            longitude = 4.5705,
            city = "Lier",
            postalCode = "2500"
        ),
        Location( // Boom
            address = "Grote Markt 1",
            latitude = 51.0889,
            longitude = 4.3666,
            city = "Boom",
            postalCode = "2850"
        )
    )

    private val categories = listOf(
        Category(
            id = "cat_tools",
            name = "Power Tools",
            description = "Professional and home improvement tools",
            iconUrl = "https://source.unsplash.com/random/100x100/?powertool"
        ),
        Category(
            id = "cat_garden",
            name = "Garden Equipment",
            description = "Tools for garden maintenance and landscaping",
            iconUrl = "https://source.unsplash.com/random/100x100/?garden"
        ),
        Category(
            id = "cat_kitchen",
            name = "Kitchen Appliances",
            description = "Professional kitchen and cooking equipment",
            iconUrl = "https://source.unsplash.com/random/100x100/?kitchen"
        ),
        Category(
            id = "cat_cleaning",
            name = "Cleaning Equipment",
            description = "Professional cleaning machines and tools",
            iconUrl = "https://source.unsplash.com/random/100x100/?cleaning"
        ),
        Category(
            id = "cat_party",
            name = "Party & Events",
            description = "Equipment for parties and events",
            iconUrl = "https://source.unsplash.com/random/100x100/?party"
        ),
        Category(
            id = "cat_sports",
            name = "Sports Equipment",
            description = "Various sports and outdoor gear",
            iconUrl = "https://source.unsplash.com/random/100x100/?sport"
        )
    )

    private val demoUsers = listOf(
        Triple("john.doe@example.com", "password123", "John Doe"),
        Triple("jane.smith@example.com", "password123", "Jane Smith"),
        Triple("mike.wilson@example.com", "password123", "Mike Wilson"),
        Triple("sarah.brown@example.com", "password123", "Sarah Brown")
    )

    private val deviceTemplates = listOf(
        Triple("Power Drill Set", "cat_tools", 15.99),
        Triple("Lawn Mower", "cat_garden", 25.99),
        Triple("Professional Stand Mixer", "cat_kitchen", 35.99),
        Triple("Pressure Washer", "cat_cleaning", 29.99),
        Triple("Party Tent", "cat_party", 45.99),
        Triple("Mountain Bike", "cat_sports", 19.99),
        Triple("Chainsaw", "cat_tools", 28.99),
        Triple("Hedge Trimmer", "cat_garden", 22.99),
        Triple("Food Processor", "cat_kitchen", 18.99),
        Triple("Steam Cleaner", "cat_cleaning", 24.99),
        Triple("Sound System", "cat_party", 55.99),
        Triple("Camping Set", "cat_sports", 32.99)
    )

    suspend fun seedData() {
        categories.forEach { category ->
            db.collection("categories").document(category.id).set(category).await()
        }

        val createdUserIds = demoUsers.mapIndexed { index, (email, password, name) ->
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = result.user?.uid ?: return@mapIndexed null

                val userLocation = flemishLocations[index % flemishLocations.size]

                val user = User(
                    id = userId,
                    email = email,
                    name = name,
                    phoneNumber = "+32${Random().nextInt(400000000) + 400000000}",
                    profileImageUrl = when {
                        name.contains("John") -> "https://source.unsplash.com/random/200x200/?portrait,man,professional"
                        name.contains("Jane") -> "https://source.unsplash.com/random/200x200/?portrait,woman,professional"
                        name.contains("Mike") -> "https://source.unsplash.com/random/200x200/?portrait,man,casual"
                        name.contains("Sarah") -> "https://source.unsplash.com/random/200x200/?portrait,woman,casual"
                        else -> "https://source.unsplash.com/random/200x200/?portrait"
                    },
                    location = userLocation,
                    rating = 4.0 + Random().nextDouble(),
                    reviewCount = Random().nextInt(20) + 1
                )

                db.collection("users").document(userId).set(user).await()
                userId
            } catch (e: Exception) {
                null
            }
        }.filterNotNull()

        createdUserIds.forEach { userId ->
            deviceTemplates.shuffled().take(4).forEach { (name, categoryId, basePrice) ->

                val baseLocation = flemishLocations.random()
                val offsetLat = (Random().nextDouble() - 0.5) * 0.02 // ~1km radius
                val offsetLng = (Random().nextDouble() - 0.5) * 0.02

                val deviceLocation = Location(
                    address = baseLocation.address,
                    latitude = baseLocation.latitude + offsetLat,
                    longitude = baseLocation.longitude + offsetLng,
                    city = baseLocation.city,
                    postalCode = baseLocation.postalCode
                )

                val device = Device(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    description = "Professional grade ${name.lowercase(Locale.ROOT)} available for rent. Well maintained and in excellent condition.",
                    category = categoryId,
                    ownerId = userId,
                    dailyPrice = basePrice + Random().nextDouble() * 10,
                    securityDeposit = basePrice * 5,
                    location = deviceLocation,
                    imageUrls = listOf(
                        "https://source.unsplash.com/random/400x300/?${name.replace(" ", "")}",
                        "https://source.unsplash.com/random/400x300/?${categoryId.substring(4)}"
                    ),
                    condition = "Excellent",
                    isAvailable = true
                )

                db.collection("devices").document(device.id).set(device).await()
            }
        }


        createdUserIds.forEach { reviewerId ->
            createdUserIds.filter { it != reviewerId }.forEach { reviewedId ->
                val review = Review(
                    id = UUID.randomUUID().toString(),
                    reviewerId = reviewerId,
                    reviewedId = reviewedId,
                    rating = Random().nextInt(3) + 3, // 3-5 stars
                    comment = "Great experience renting from this user!",
                    createDate = System.currentTimeMillis() - Random().nextInt(30) * 24 * 60 * 60 * 1000L
                )

                db.collection("reviews").document(review.id).set(review).await()
            }
        }
    }
}