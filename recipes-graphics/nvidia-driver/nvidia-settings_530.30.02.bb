# Copyright (c) 2023, NVIDIA CORPORATION. All rights reserved.
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

require nvidia-driver-common.inc

SRC_COMMON_DEBS = "${BPN}_${PV}-0ubuntu1_${DEB_PKGARCH}.deb;subdir=${BP};name=${DEB_PKGARCH}"
SRC_URI[arm64.sha256sum] = "bf4722b05002a8b8c5b138c0a3224086adb76044e2c3fb1243a246be6d857452"
SRC_URI[amd64.sha256sum] = "397b2c138371bf1e60bf651862d2eed85ffd7352d2999173ba93c6cca73613d5"

RDEPENDS:${PN} += " \
    cairo \
    gdk-pixbuf \
    glib-2.0 \
    gtk+ \
    gtk+3 \
    jansson \
    libvdpau \
    libx11 \
    libxnvctrl0 \
    libxxf86vm \
    pango \
    pkgconfig \
"
