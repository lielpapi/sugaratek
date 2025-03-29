package com.example.sugarate.shared

import androidx.lifecycle.ViewModel
import com.example.sugarate.profile.UserMetaData


class SharedViewModel : ViewModel() {
    var userMetaData: UserMetaData = UserMetaData(fullName = "",
        email = "", profilePhoto = "")
}