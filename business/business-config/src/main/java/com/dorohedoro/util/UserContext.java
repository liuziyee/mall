package com.dorohedoro.util;

import com.dorohedoro.dto.UserDTO;

public class UserContext {
    private static final ThreadLocal<UserDTO> threadLocal = new ThreadLocal<>();

    public static UserDTO getUserData() {
        return threadLocal.get();
    }

    public static void setUserData(UserDTO userDTO) {
        threadLocal.set(userDTO);
    }

    public static void clearUserData() {
        if (threadLocal.get() != null) {
            threadLocal.remove();
        }
    }
}
