package pnj.uas.ti.marwah_nur_shafira.sql

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseOpenHelper(
    context: Context
) : SQLiteOpenHelper(context, "db_wisata", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE tb_wisata (id INTEGER PRIMARY KEY AUTOINCREMENT, nama TEXT, no_tlp TEXT, " +
                "tahun INTEGER, longitude TEXT, latitude TEXT, gambar BLOB)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun deleteAllData(): Boolean {
        val db = this.writableDatabase
        val deletedRows = db.delete("tb_wisata", null, null)
        db.close()
        return deletedRows > 0
    }

}

