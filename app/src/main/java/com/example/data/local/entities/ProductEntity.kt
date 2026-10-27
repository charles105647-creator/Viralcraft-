package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val description: String,
    val originalPrice: Double,
    val promoPrice: Double,
    val discountPercent: Int = (((originalPrice - promoPrice) / originalPrice) * 100).toInt().coerceAtLeast(0),
    val couponCode: String = "",
    val keyFeatures: String, // Comma-separated or bullet list
    val targetAudience: String = "Geral",
    val imageDrawableRes: String = "showcase_smartwatch_1790132114921",
    val customImageUri: String = "",
    val productLink: String = "https://shopee.com.br/produto-exemplo",
    val createdAtEpoch: Long = System.currentTimeMillis()
)
