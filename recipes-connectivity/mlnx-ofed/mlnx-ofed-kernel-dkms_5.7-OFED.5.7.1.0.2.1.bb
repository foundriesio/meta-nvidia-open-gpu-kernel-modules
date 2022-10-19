# Copyright (c) 2022, NVIDIA CORPORATION. All rights reserved.
#
# Permission is hereby granted, free of charge, to any person obtaining a
# copy of this software and associated documentation files (the "Software"),
# to deal in the Software without restriction, including without limitation
# the rights to use, copy, modify, merge, publish, distribute, sublicense,
# and/or sell copies of the Software, and to permit persons to whom the
# Software is furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
# THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
# FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
# DEALINGS IN THE SOFTWARE.

SUMMARY = "Mellanox ofed-kernel-dkms"
LICENSE = "CLOSED"

DEB_PACKAGE_SUFFIX = "all"

require mlnx-ofed-package.inc

SRC_URI += " \
    file://fix-kernel-build.patch \
"

inherit autotools module

DEPENDS = " \
    coreutils-native \
"

do_configure() {
    cp -rf ${S}/usr/src/mlnx-ofed-kernel-5.7/* ${B}

    ./configure \
        --skip-autoconf \
        --with-core-mod \
        --with-mlx5-mod \
        --with-user_access-mod \
        --with-linux=${STAGING_KERNEL_DIR} \
        --with-linux-obj=${STAGING_KERNEL_BUILDDIR} \
        --kernel-sources=${STAGING_KERNEL_DIR} \
        --kernel-version=${KERNEL_VERSION}

    cd compat
    ./autogen.sh
    cp -f Makefile.real Makefile
    cp -f Makefile.real Makefile.in
    ./configure ${CONFIGUREOPTS} \
        --with-linux=${STAGING_KERNEL_DIR} \
        --with-linux-obj=${STAGING_KERNEL_BUILDDIR} \
        --with-njobs=12
}

MODULES_INSTALL_TARGET = "install_modules"

do_install:prepend() {
    install -d ${D}/usr/src/mlnx-ofed-kernel-dkms
    cp -rd --no-preserve=ownership ${S}/usr/src/mlnx-ofed-kernel-5.7/* ${D}/usr/src/mlnx-ofed-kernel-dkms
    export INSTALL_MOD_PATH=${D}
}

FILES:${PN}-dev += " \
    /usr/src/mlnx-ofed-kernel-dkms \
"

SYSROOT_DIRS = " \
    /usr/src/mlnx-ofed-kernel-dkms \
    /usr/include/mlnx-ofed-kernel-dkms \
"
