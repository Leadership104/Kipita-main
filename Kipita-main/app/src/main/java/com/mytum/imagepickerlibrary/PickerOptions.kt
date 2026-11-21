package com.mytum.imagepickerlibrary

import android.os.Parcelable
import com.mytum.imagepickerlibrary.model.PickExtension
import com.mytum.imagepickerlibrary.model.PickerType
import kotlinx.parcelize.Parcelize

/**
 * Data class to manage the picker options from bottom sheet to main activity.
 */
@Parcelize
data class PickerOptions(
    val pickerType: PickerType,
    val showCountInToolBar: Boolean,
    val showFolders: Boolean,
    val allowMultipleSelection: Boolean,
    val maxPickCount: Int,
    val maxPickSizeMB: Float,
    val pickExtension: PickExtension,
    val showCameraIconInGallery: Boolean,
    val isDoneIcon: Boolean,
    val openCropOptions: Boolean,
    val openSystemPicker: Boolean,
    val compressImage: Boolean
) : Parcelable {
    companion object {
        fun default(): PickerOptions {
            return PickerOptions(
                pickerType = PickerType.GALLERY,
                showCountInToolBar = true,
                showFolders = true,
                allowMultipleSelection = true,
                maxPickCount = 5,
                maxPickSizeMB = 2.5f,
                pickExtension = PickExtension.ALL,
                showCameraIconInGallery = true,
                isDoneIcon = true,
                openCropOptions = false,
                openSystemPicker = false,
                compressImage = false
            )
        }
    }
}