/*
 *  Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This code is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License version 2 only, as
 *  published by the Free Software Foundation.  Oracle designates this
 *  particular file as subject to the "Classpath" exception as provided
 *  by Oracle in the LICENSE file that accompanied this code.
 *
 *  This code is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  version 2 for more details (a copy is included in the LICENSE file that
 *  accompanied this code).
 *
 *  You should have received a copy of the GNU General Public License version
 *  2 along with this work; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *   Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 *  or visit www.oracle.com if you need additional information or have any
 *  questions.
 *
 */

/*
 * ===========================================================================
 * (c) Copyright IBM Corp. 2021, 2021 All Rights Reserved
 * ===========================================================================
 */

package jdk.internal.foreign;

import jdk.internal.foreign.abi.SharedUtils;

import static jdk.incubator.foreign.MemoryLayouts.ADDRESS;

public enum CABI {
    SysV,
    Win64,
    AArch64,
    SysVppc64le,
    SysVs390x,
    AIX;

    private static CABI current;

    static {
        String arch = System.getProperty("os.arch");
        String os = System.getProperty("os.name");
        long addressSize = ADDRESS.bitSize();
        // might be running in a 32-bit VM on a 64-bit platform.
        // addressSize will be correctly 32
        if ((arch.equals("amd64") || arch.equals("x86_64")) && addressSize == 64) {
            if (os.startsWith("Windows")) {
                current = Win64;
            } else {
                current = SysV;
            }
        } else if (arch.equals("aarch64")) {
            current = AArch64;
        } else if (arch.startsWith("ppc64")) {
            if (os.startsWith("Linux")) {
                current = SysVppc64le;
            } else {
                current = AIX;
            }
        } else if (arch.equals("s390x")) {
            if (os.startsWith("Linux")) {
                current = SysVs390x;
            }
        } else {
            throw new ExceptionInInitializerError(
                "Unsupported os, arch, or address size: " + os + ", " + arch + ", " + addressSize);
        }
    }

    public static CABI current() {
        return current;
    }
}
