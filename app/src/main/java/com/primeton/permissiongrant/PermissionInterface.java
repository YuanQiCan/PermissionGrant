package com.primeton.permissiongrant;

public interface PermissionInterface {
    /** 获得的权限数组
     * @param permissions
     */
    public void accept(String[] permissions);  //获得权限组

    /**被拒绝的权限数组
     * @param permissions
     */
    public void denied(String[] permissions);   //拒绝的权限组

    /**拒绝并不在提示的权限数组
     * @param permissions
     */
    public void allDenied(String[] permissions);  // 不在询问的权限组  （拒绝一次之后，选择不在拒绝，再次申请才会调用）
}
