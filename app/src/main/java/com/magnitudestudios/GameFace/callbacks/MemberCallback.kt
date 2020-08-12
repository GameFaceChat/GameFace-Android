package com.magnitudestudios.GameFace.callbacks

import com.magnitudestudios.GameFace.pojo.EnumClasses.MemberStatus
import com.magnitudestudios.GameFace.pojo.VideoCall.Member

interface MemberCallback {
    fun onNewMember(member: Member)
    fun onMemberStatusChanged(uid: String, newStatus: MemberStatus)
}