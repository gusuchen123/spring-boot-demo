package com.example.lock.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-10-13 18:19
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LockConstants {
    public static final String OK = "OK";

    /**
     * NX: Only set the key if it not already exists;
     */
    public static final String NOT_EXISTS = "NX";

    /**
     * XX: Only set the key if it already exists;
     */
    public static final String EXISTS = "XX";

    /**
     * expire time units: EX = seconds;
     */
    public static final String SECONDS = "EX";

    /**
     * expire time units: PX = milliseconds;
     */
    public static final String MILLISECONDS = "PX";
}
