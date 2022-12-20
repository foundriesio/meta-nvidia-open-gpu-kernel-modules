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

SUMMARY = "GL/GLES/EGL/GLX/WGL Loader-Generator based on the official specs."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=13b95fe9b2a06bd9032d0b3aacebc7c0"

SRC_URI = "git://github.com/Dav1dde/glad.git;branch=master;protocol=https"
SRCREV = "1ecd45775d96f35170458e6b148eb0708967e402"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE:append = " \
    -D BUILD_SHARED_LIBS:BOOL=ON \
    -D GLAD_INSTALL:BOOL=ON \
"

do_compile[network] = "1"

do_install:append () {
    rm ${D}${includedir}/KHR/khrplatform.h
}

FILES_SOLIBSDEV = ""
SOLIBS = "*.so*"
