package com.jaredhuang.bookdb.help.permission

interface OnPermissionsDeniedCallback {
    fun onPermissionsDenied(requestCode: Int, deniedPermissions: Array<String>)
}
