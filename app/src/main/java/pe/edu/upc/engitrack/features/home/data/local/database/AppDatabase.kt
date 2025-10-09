package pe.edu.upc.engitrack.features.home.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pe.edu.upc.engitrack.features.home.data.local.dao.ProductDao
import pe.edu.upc.engitrack.features.home.data.local.models.ProductEntity


@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

}