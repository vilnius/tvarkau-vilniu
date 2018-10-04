package lt.vilnius.tvarkau.viewmodel

import android.arch.lifecycle.LiveData
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity

/**
 *
 */
class ContactDataLiveData(
    private val activity: AppCompatActivity
) : LiveData<List<String>>(), LoaderManager.LoaderCallbacks<Cursor> {

    override fun onActive() {
        super.onActive()
        activity.supportLoaderManager.initLoader(LOADER_ID, null, this)
    }

    override fun onInactive() {
        super.onInactive()
        activity.supportLoaderManager.destroyLoader(LOADER_ID)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val uri = Uri.withAppendedPath(
            ContactsContract.Profile.CONTENT_URI,
            ContactsContract.Contacts.Data.CONTENT_DIRECTORY
        )
        val sortOrder = ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"
        val projection = ProfileQuery.PROJECTION
        val selection = ContactsContract.Contacts.Data.MIMETYPE + " = ?"
        val selectionArgs = arrayOf(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)

        return CursorLoader(
            activity,
            uri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        val emails = mutableListOf<String>()

        value = if (data == null) {
            null
        } else {
            data.moveToFirst()
            while (!data.isAfterLast) {
                emails.add(data.getString(ProfileQuery.ADDRESS))
                data.moveToNext()
            }

            emails
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        value = null
    }

    companion object {
        private const val LOADER_ID = 100
    }


    object ProfileQuery {
        val PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.IS_PRIMARY
        )
        const val ADDRESS = 0
    }
}
