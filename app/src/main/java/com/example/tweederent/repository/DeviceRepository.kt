package com.example.tweederent.repository

import android.net.Uri
import com.example.tweederent.data.Device
import com.example.tweederent.data.Location
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

open class DeviceRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val devicesCollection = db.collection("devices")

    suspend fun addDevice(device: Device, imageUris: List<Uri>): Result<String> = try {
        // First upload images and get their URLs
        val imageUrls = uploadImages(imageUris)

        // Create device with image URLs and current user as owner
        val deviceWithImages = device.copy(
            id = UUID.randomUUID().toString(),
            ownerId = auth.currentUser?.uid ?: throw IllegalStateException("No user logged in"),
            imageUrls = imageUrls,
            createDate = System.currentTimeMillis()
        )

        // Link met Firestore
        devicesCollection.document(deviceWithImages.id).set(deviceWithImages).await()
        Result.success(deviceWithImages.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun uploadImages(imageUris: List<Uri>): List<String> {
        return imageUris.map { uri ->
            val ref = storage.reference.child("devices/${UUID.randomUUID()}")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        }
    }

    suspend fun getDevice(id: String): Result<Device> = try {
        val device = devicesCollection.document(id).get().await()
            .toObject(Device::class.java)
            ?: throw IllegalStateException("Device not found")
        Result.success(device)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getDevicesByOwner(ownerId: String): Result<List<Device>> = try {
        val devices = devicesCollection
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()
            .toObjects(Device::class.java)
        Result.success(devices)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateDevice(device: Device): Result<Unit> = try {
        devicesCollection.document(device.id).set(device).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteDevice(deviceId: String): Result<Unit> = try {
        devicesCollection.document(deviceId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun searchDevices(
        query: String = "",
        category: String? = null,
        location: Location? = null,
        radius: Double? = null
    ): Result<List<Device>> = try {
        var ref = devicesCollection.whereEqualTo("isAvailable", true)

        if (category != null) {
            ref = ref.whereEqualTo("category", category)
        }

        val devices = ref.get().await().toObjects(Device::class.java)

        // Filter by search query and location if provided
        val filteredDevices = devices.filter { device ->
            val matchesQuery = query.isEmpty() ||
                    device.name.contains(query, ignoreCase = true) ||
                    device.description.contains(query, ignoreCase = true)

            val matchesLocation = if (location != null && radius != null) {
                calculateDistance(
                    location.latitude, location.longitude,
                    device.location.latitude, device.location.longitude
                ) <= radius
            } else true

            matchesQuery && matchesLocation
        }

        Result.success(filteredDevices)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        // Haversine formula de afstandsbepaling tussen 2 coördinaten
        val r = 6371 // omtrek van de aarde
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon/2) * Math.sin(dLon/2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
        return r * c
    }
}