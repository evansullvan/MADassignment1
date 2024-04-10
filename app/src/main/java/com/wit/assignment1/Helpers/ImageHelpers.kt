package com.wit.assignment1.Helpers

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.wit.assignment1.R

fun showImagePicker(intentLauncher : ActivityResultLauncher<Intent>) {
    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
    chooseFile.type = "image/*"
    chooseFile = Intent.createChooser(chooseFile, "Select image")
    intentLauncher.launch(chooseFile)
}