package com.mfeldsztejn.ringtest.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

/**
 * Based on [ActivityResultContracts.CreateDocument] with the ability to set the mimeType
 */
class CreateDocumentContract(private val mimeType: String = "image/png") : ActivityResultContract<String, Uri?>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, input)
        }

    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }
}