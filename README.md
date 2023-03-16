# OpenEmbedded/Yocto layer for NVIDIA Holoscan

This layer adds OpenEmbedded recipes and sample build configurations to build
BSPs for NVIDIA Holoscan Developer Kits that feature support for discrete GPUs
(dGPU), Rivermax, AJA Video Systems I/O boards, and the NVIDIA Holoscan SDK.
These BSPs are built on a developer's host machine and are then flashed onto a
Holoscan Developer Kit using provided scripts.

This is an add-on layer to the [meta-tegra](https://github.com/OE4T/meta-tegra)
BSP layer with additions to enable the discrete GPU (dGPU) and other hardware
drivers and toolkits that are used by the NVIDIA Holoscan SDK.

## Supported Boards

* Clara AGX Developer Kit (clara-agx-xavier-devkit)
* NVIDIA IGX Orin Developer Kit ES (holoscan-devkit)
* NVIDIA IGX Orin Developer Kit (igx-orin-devkit)

## System Requirements

Building a BSP for NVIDIA Holoscan requires a significant amount of system
resources. Available disk space is the only strict requirement that must be
met, with **a minimum of 300GB of free disk space required for a build** using
the default configuration as described in this document. It is recommended,
however, that the development system have many CPU cores, a fast internet
connection, and a large amount of memory and disk bandwidth in order to
minimize the amount of time that is required to build the BSP.

For example, on a system with the following specifications:

* CPU: Intel Core i7-7800X, 6 Cores @ 3.50GHz
* RAM: 32GB DDR4-2133
* Disk: Samsung EVO 870 2TB SSD
* Network: 1 Gigabit Fiber Internet
* Operating System: Ubuntu 18.04

a complete build using the `core-image-x11` target and the default
package configuration (including CUDA, TensorRT, and Holoscan SDK) takes:

* Build Time: 3 hours and 5 minutes
* Package Downloads: 22GB
* Disk Space Used: 265GB

## Build Environment Options

There are two options available to set up a build environment and start
building Holoscan BSP images using this layer. The first sets up a
traditional local build environment in which all dependencies are fetched and
installed manually by the developer directly on their host machine. The second
uses a Holoscan OpenEmbedded/Yocto Build Container that is provided by NVIDIA
which contains all of the dependencies and configuration scripts such that the
entire process of building and flashing a BSP can be done with just a few simple
commands.

### 1. Local Build Environment

This section outlines what is needed to use this layer for a local build
environment in which all dependencies are manually downloaded and installed
on the host machine.

#### Dependencies

Unless otherwise stated, the following dependencies should all be cloned into
the same working directory that this `meta-tegra-holoscan` repo has been cloned
into.

Also note that these dependencies are being actively developed, so
compatibility can not be guaranteed when using the latest versions. Each
dependency below has a commit ID that has been verified to work with this
layer and thus should be used to ensure the build completes. To use these
commit IDs, change into the directory that a dependency has been cloned
into and then run `git checkout {commit id}`.

* #### Poky: https://git.yoctoproject.org/poky/ (commit: `65dafea2`, tag: `kirkstone-4.0.7`)

    The Poky Build Tool and Metadata for the Yocto Project.

    See the [Yocto Project System Requirements](https://docs.yoctoproject.org/ref-manual/system-requirements.html#required-packages-for-the-build-host)
    for the most up to date list of requirements that are needed for Yocto.
    Specifically, see the `Essentials: Packages needed to build an image on a
    headless system` section for your particular operating system. For Ubuntu
    and Debian operating systems, this list is currently as follows:

    ```sh
    sudo apt install gawk wget git diffstat unzip texinfo gcc build-essential \
         chrpath socat cpio python3 python3-pip python3-pexpect xz-utils \
         debianutils iputils-ping python3-git python3-jinja2 libegl1-mesa \
         libsdl1.2-dev pylint3 xterm python3-subunit mesa-common-dev zstd \
         liblz4-tool
    ```

* #### Additional Metadata Layers

    The following additional layers are required and must be added to the
    `BBLAYERS` list (along with this `meta-tegra-holoscan` layer itself) in the
    `build/conf/bblayers.conf` configuration file that is created by the
    `oe-init-build-env` script. Note that the paths in this configuration file
    must be full paths; see the template file in
    `meta-tegra-holoscan/env/templates/conf/bblayers.conf` for an example when
    these layers are cloned into a `/workspace` root directory.

    Note that the repo used here for the `meta-tegra` layer is a fork of the
    official `meta-tegra` repo found at https://github.com/OE4T/meta-tegra.
    Changes from this fork will be upstreamed to the official repo when
    appropriate, but this fork may otherwise contain Holoscan-specific changes
    that aren't suitable for the upstream repo.

    | Layer | Repo | Commit |
    | -------------- | ---- | ------ |
    | meta-openembedded/meta-oe <br/> meta-openemdedded/meta-filesystems <br/> meta-openembedded/meta-networking <br/> meta-openembedded/meta-python | https://github.com/openembedded/meta-openembedded | `278ec081` |
    | meta-virtualization | https://git.yoctoproject.org/meta-virtualization | `9a94fa2a` |
    | meta-tegra | https://github.com/nvidia-holoscan/meta-tegra | `f779da5c` |

* #### Proprietary NVIDIA Binary Packages

    Some of the recipes contained in this layer depend on proprietary NVIDIA
    binary packages that can only be downloaded from the NVIDIA developer
    website using an NVIDIA developer account, and so these packages can not be
    automatically downloaded by the bitbake build process. If these components
    are to be included in the final BSP image, the binary packages must be
    manually downloaded and placed into the corresponding recipe directory
    before building.

    The current list of manually downloaded packages is as follows:

    * #### cuDNN (8.7.0.84)

      Download: https://developer.nvidia.com/downloads/c118-cudnn-linux-sbsa-87084cuda11-archivetarz  
      Local Destination: `meta-tegra-holoscan/recipes-devtools/cudnn/files/cudnn-linux-sbsa-8.7.0.84_cuda11-archive.tar.xz`

    * #### TensorRT (8.5.3.1)

      Download: https://developer.nvidia.com/downloads/compute/machine-learning/tensorrt/secure/8.5.3/tars/TensorRT-8.5.3.1.Ubuntu-20.04.aarch64-gnu.cuda-11.8.cudnn8.6.tar.gz  
      Local Destination: `meta-tegra-holoscan/recipes-devtools/tensorrt/files/TensorRT-8.5.3.1.Ubuntu-20.04.aarch64-gnu.cuda-11.8.cudnn8.6.tar.gz`

    * #### GXF (2.5.0)

      Download: https://catalog.ngc.nvidia.com/orgs/nvidia/teams/clara-holoscan/resources/gxf_arm64_holoscan_sdk  
      Local Destination: `meta-tegra-holoscan/recipes-devtools/gxf/files/gxf_22.11_20230223_6b2e34ec_holoscan-sdk_arm64.tar.gz`

      > **_Note:_** When GXF is downloaded from NGC it will download a ZIP
      > archive named `files.zip` that contains the above `tar.gz` file; extract
      > this `tar.gz` file from the archive and move it to the destination given
      > above.

    * #### Rivermax SDK (1.20.10)

      Download: https://developer.nvidia.com/networking/secure/rivermax-linux-sdk/installation-package/version-1.20.x/1.20/rivermax_ubuntu2004_1.20.10.tar.gz  
      Local Destination: `meta-tegra-holoscan/recipes-connectivity/rivermax/files/rivermax_ubuntu2004_1.20.10.tar.gz`

    * #### Nsight Systems (2023.1.1)

      Download: https://developer.nvidia.com/downloads/nsight-systems20231-202311127-1-arm64deb  
      Local Destination: `meta-tegra-holoscan/recipes-devtools/nsight-systems/files/nsight-systems-2023.1.1_2023.1.1.127-1_arm64.deb`

#### iGPU and dGPU Configurations

This layer provides both iGPU and dGPU support for the Holoscan platforms
via the `conf/holoscan-igpu.conf` and `conf/holoscan-dgpu.conf` configuration
files, respectively.

When using the iGPU configuration, the majority of the runtime components come
from the standard Tegra packages used by the `meta-tegra` layer. When using the
dGPU configuration, some of the Tegra packages are overridden with drivers and
binary packages defined by this recipe layer that are needed to support the dGPU.

The versions of the components used for iGPU and dGPU mode differ slightly, as
given in the following table:

| Component       | iGPU       | dGPU             |
| --------------- | ---------- | ---------------- |
| Display Drivers | L4T 35.1.0 | OpenRM 525.85.05 |
| CUDA            | 11.4.14    | 11.8.0           |
| cuDNN           | 8.4.1.50   | 8.7.0.84         |
| TensorRT        | 8.4.1      | 8.5.3.1          |

#### Build Configuration

To configure a Holoscan BSP, the `MACHINE` setting in the
`build/conf/local.conf` configuration file that is initially generated by
`oe-init-build-env` must be changed to one of the boards supported by this
layer (see above), and the iGPU or dGPU configuration must be selected by
including either `conf/holoscan-igpu.conf` or `conf/holoscan-dgpu.conf`,
respectively:

```
MACHINE ??= "igx-orin-devkit"
require conf/holoscan-dgpu.conf
```

> **_Note:_** If the configuration is switched between iGPU and dGPU, the
> graphics driver packages need to be cleaned before building the BSP in order
> to prevent file conflict errors. To clean these packages, issue these
> commands:
>
> ```sh
> $ bitbake nvidia-display-driver -c clean
> $ bitbake nvidia-open-gpu-kernel-modules -c clean
> ```

Additional components from this layer can then be added to the BSP by appending
them to `CORE_IMAGE_EXTRA_INSTALL`. For example, to install the Holoscan SDK
and its dependencies, add the following:

```
CORE_IMAGE_EXTRA_INSTALL:append = " \
    holoscan-sdk \
"
```

A template configuration file that does the above is provided in
`env/templates/conf/local.conf` and can be used as-is to replace the initial
`local.conf` file that was generated by `oe-init-build-env`. This template
also includes additional documentation about components and features provided by
this layer such as enabling an AJA Video I/O device. This additional
documentation can be seen by scrolling to the `BEGIN NVIDIA CONFIGURATION`
section at the bottom of the file.

##### Adding Additional Kernel Modules

The machine configuration for a Holoscan devkit includes the minimal set of
kernel modules required to support the onboard components, but it does not
include modules to support additional peripherals such as USB cameras or
wireless keyboards and mice. If such peripherals will be used, it will be
required to add the corresponding kernel modules to the image to support these
devices. For example, to enable a Logitech wireless keyboard or mouse, the
following kernel modules are needed:

```
CORE_IMAGE_EXTRA_INSTALL:append = " \
    kernel-module-hid-logitech-dj \
    kernel-module-hid-logitech-hidpp \
"
```

If you are unsure what drivers are needed, the generic `kernel-modules` package
can be added to the install list instead to install all of the upstream kernel
modules. This will increase build time and image size, so it is suggested to
install just the specific kernel modules that are actually needed if possible.
Note that the `kernel-modules` package has been added to the template
configuration in `env/templates/conf/local.conf` to improve the out-of-the-box
support for additional peripherals during the initial development phase.

##### Enabling PREEMPT_RT patch

`PREEMPT_RT` patch support for the Linux kernel is added by including the
`conf/rt-patch.conf` file in `build/conf/local.conf`, like the following line:

```
require conf/rt-patch.conf
```

The `PREEMPT_RT` patch is currently only supported with iGPU configuration,
enabling the `PREEMPT_RT` patch with dGPU configuration will lead to build 
failures. 

##### Enabling Rivermax

Rivermax support is added by including the `conf/rivermax.conf` file:

```
require conf/rivermax.conf
```

Using Rivermax requires a valid license file, and the `rivermax-license` recipe
is responsible for installing the Rivermax license file provided by
`meta-tegra-holoscan/recipes-connectivity/rivermax/files/rivermax.lic`.
This file is an empty (invalid) license file by default, and must be replaced
with a valid Rivermax license file in order to fully enable Rivermax support.
Alternatively, the license file can be copied to the device at runtime by
replacing `/opt/mellanox/rivermax/rivermax.lic`.

##### Enabling AJA Video Devices

To enable support for AJA Video I/O devices, the AJA NTV2 kernel module can be
built into the image by adding the `kernel-module-ajantv2` component to
`CORE_IMAGE_EXTRA_INSTALL` as described above:

```
CORE_IMAGE_EXTRA_INSTALL:append = " kernel-module-ajantv2"
```

#### Building and Flashing

Building a BSP is done with `bitbake`; for example, to build a `core-image-sato`
image, use the following:

```sh
$ bitbake core-image-sato
```

> **_Note:_** If the `bitbake` command is not found, ensure that the current
> shell has been initialized using `source poky/oe-init-build-env`.
> This script will add the required paths to the `PATH` environment variable so
> that the `bitbake` command can be run from any directory.

> **_Note:_** For the list of different image targets that are available to build,
> see the [Yocto Project Images List](https://docs.yoctoproject.org/ref-manual/images.html).

> **_Note:_** If the build fails due to unavailable resource errors, try the
> build again. Builds are extremely resource-intensive, and having a number of
> particularly large tasks running in parallel can exceed even 32GB of system
> memory usage. Repeating the build can often reschedule the tasks so that
> they can succeed. If errors are still encountered, try lowering the value
> of [BB_NUMBER_THREADS](https://docs.yoctoproject.org/ref-manual/variables.html#term-BB_NUMBER_THREADS)
> in `build/conf/local.conf` to reduce the maximum number of tasks that BitBake
> should run in parallel at any one time.

> **_Note:_** Race conditions have been encountered that lead to errors during
> the `do_rootfs` stage of the build such as `Couldn't find anything to satisfy
> 'rivermax'`. If this occurs, try cleaning the failing package, build the
> package by itself, then build the image again. For example, if the `rivermax`
> package fails to install, try the following:
>
> ```sh
> $ bitbake rivermax -c cleansstate
> $ bitbake rivermax
> $ bitbake core-image-sato
> ```

Using the configuration described above, this will build the BSP image and write
the output to

```
build/tmp/deploy/images/igx-orin-devkit/core-image-sato-igx-orin-devkit.tegraflash.tar.gz
```

The above file can then be extracted and the `doflash.sh` script that it
includes can be used to flash the device while it is in recovery mode and
connected to the host via the USB-C debug port:

```sh
$ tar -xf ${image_path}
$ sudo ./doflash.sh
```

> **_Note:_** If the `doflash.sh` command fails due to a `No such file: 'dtc'`
> error, install the device tree compiler (`dtc`) using the following:
>
> ```sh
> $ sudo apt-get install device-tree-compiler
> ```

> **_Note:_** For instructions on how to put the developer kit into recovery
> mode, see the developer kit user guide:
>  - [Clara AGX Developer Kit User Guide](https://developer.nvidia.com/clara-agx-developer-kit-user-guide).
>  - [IGX Orin Developer Kit User Guide](https://developer.nvidia.com/igx-orin-developer-kit-user-guide).

Once flashed, the Holoscan Developer Kit can then be disconnected from the host
system and booted. A display, keyboard, and mouse should be attached to the
developer kit before it is booted. The display connection depends on the GPU
configuration that was used for the build: the iGPU configuration uses the
onboard Tegra display connection while the dGPU configuration uses one of the
connections on the discrete GPU. Please refer to the developer kit user guide
for diagrams showing the locations of these display connections. During boot
you will see a black screen with only a cursor for a few moments before an X11
terminal or GUI appears (depending on your image type).

> **_Note:_** If the monitor never receives a signal there may be an issue
> configuring the monitor during the initial boot process. If this occurs,
> the `xrandr` utility can generally be used from a remote shell to display
> the available monitor modes and to select a current mode. For example, to
> configure a 1920x1080 display connected to the HDMI-0 output, use the
> following:
>
> ```sh
> $ export DISPLAY=:0
> $ xrandr --output HDMI-0 --mode 1920x1080
> ```

#### Running the Holoscan SDK and HoloHub Applications

When the `holoscan-sdk` component is installed, the Holoscan SDK is installed
into the image in the `/opt/nvidia/holoscan` directory, with examples present in
the `examples` subdirectory. Due to relative data paths being used by the apps,
these examples should be run from the `/opt/nvidia/holoscan` directory. To run
the C++ version of an example, simply run the executable in the example's `cpp`
subdirectory:

```sh
$ cd /opt/nvidia/holoscan
$ ./examples/hello_world/cpp/hello_world
```

To run the Python version of an example, run the application in the example's
`python` subdirectory using `python3`:

```sh
$ cd /opt/nvidia/holoscan
$ python3 ./examples/hello_world/python/hello_world.py
```

When the `holohub-apps` component is installed, the HoloHub sample applications
are installed into the image in the `/opt/nvidia/holohub` directory, with the
applications present in the `applications` subdirectory. Due to relative data
paths being used by the apps, these applications should be run from the
`/opt/nvidia/holohub` directory. To run the C++ version of an application,
simply run the executable in the applications's `cpp` subdirectory:

```sh
$ cd /opt/nvidia/holohub
$ ./applications/endoscopy_tool_tracking/cpp/endoscopy_tool_tracking
```

To run the Python version of an application, run the application in the
`python` subdirectory using `python3`:

```sh
$ cd /opt/nvidia/holohub
$ python3 ./applications/endoscopy_tool_tracking/python/endoscopy_tool_tracking.py
```

Note that the first execution of the samples will rebuild the model engine files
and it will take a few minutes before the application fully loads. These engine
files are then cached and will significantly reduce launch times for successive
executions.

### 2. Holoscan OpenEmbedded/Yocto Build Container

Instead of downloading and installing all of the build tools, dependencies, and
proprietary binaries manually, NVIDIA also provides a [Holoscan OpenEmbedded/Yocto Build
Container](https://catalog.ngc.nvidia.com/orgs/nvidia/teams/clara-holoscan/containers/holoscan-oe-builder)
on the [NVIDIA GPU Cloud (NGC)](https://catalog.ngc.nvidia.com/) website.  This
container image includes all of the tools and dependencies that are needed
either within the container or as part of a setup script that initializes a
local build tree, and it simplifies the process such that building and flashing
a Holoscan BSP can be done in just a few simple commands. See
[env/README.md](env/README.md) for documentation on the Holoscan OpenEmbedded/Yocto
Build Container.

> **_Note:_** the `env` directory in this repository contains the scripts and
> `Dockerfile` that are used to build the Holoscan OpenEmbedded/Yocto Build
> Container image, and can even be used to build the container image locally if
> one so desires.

## Debugging with GDB

Debugging applications on the target can be done using GDB either directly on
the target or remotely using a remote GDB connection. In either scenario, the
debugging tools and symbols can be included in the image by adding the
following to `build/conf/local.conf`:

```
EXTRA_IMAGE_FEATURES:append = " tools-debug dbg-pkgs"
```

Note that in both the local and remote debugging cases the GDB Text User
Interface (TUI) mode is enabled and can be entered by adding the `-tui`
argument to the `gdb` commands below, or toggled using the `C-x a` key binding
once GDB has been launched. For more information on debugging with GDB, see the
[Debugging with GDB](https://ftp.gnu.org/old-gnu/Manuals/gdb/html_chapter/gdb_toc.html)
and [GDB Text User Interface](https://ftp.gnu.org/old-gnu/Manuals/gdb/html_chapter/gdb_19.html#SEC197)
documentation.

### Local Debugging

For debugging locally on the device, the source code packages should also be
installed by adding the following:

```
EXTRA_IMAGE_FEATURES:append = " src-pkgs"
```

With the debugging tools, symbols, and source code installed on the device, an
application can be debugged locally by running `gdb [executable]`, e.g.:

```sh
$ cd /workspace
$ gdb ./apps/multiai/cpp/multiai
```

### Remote Debugging

#### 1. Installing the SDK on the Host

Debugging remotely requires the SDK for the image to be built and installed
on the host device from which debugging will be performed. To build the SDK
package for an image (e.g. `core-image-sato`), run the following:

```sh
$ bitbake core-image-sato -c populate_sdk
```

Once built, the script to install the SDK will be present in
`build/tmp/deploy/sdk`. To install the SDK, run the script that corresponds to
the image. For example, to install the `core-image-sato` SDK, run the following:

```sh
$ ./build/tmp/deploy/sdk/poky-glibc-x86_64-core-image-sato-armv8a-igx-orin-devkit-toolchain-4.0.7.sh
```

Follow the prompts to specify the install path for the SDK. The rest of these
instructions will assume that the default install path of `/opt/poky/4.0.7` is
used.

#### 2. Running the Remote Debugging Server on the Target

Launch the application on the target using the `gdbserver` command along with
the target's network address and port that should be used for the remote
debugging connection:

```sh
$ cd /workspace
$ gdbserver 192.168.0.100:1234 ./apps/multiai/cpp/multiai
Process ./apps/multiai/cpp/multiai created; pid = 1432
Listening on port 1234
```

#### 3. Connecting to the Remote Debugging Session

The SDK installed on the host includes an `environment-setup-armv8a-poky-linux`
script that must be sourced from any terminal before the SDK can be used:

```sh
$ source /opt/poky/4.0.7/environment-setup-armv8a-poky-linux
```

This environment provides the `$GDB` environment variable that points to the
GDB executable that must be used for the remote debugging. Launch this
`$GDB` executable then connect to the remote target using the
`target remote [ip]:[port]` command:

```sh
$ $GDB
GNU gdb (GDB) 11.2
...
(gdb) target remote 192.168.0.100:1234
Remote debugging using 192.168.0.100:1234
Reading /workspace/apps/multiai/cpp/multiai from remote target...
Reading symbols from target:/workspace/apps/multiai/cpp/multiai...
Reading /lib/ld-linux-aarch64.so.1 from remote target...
0x0000fffff7fd9d00 in _start () from target:/lib/ld-linux-aarch64.so.1
(gdb)
```

While the symbols will be loaded remotely from the target, the path to the
source code must be remapped to the local host path for the source using the
`set substitute-path` command. Assuming the path of the host build tree is in
`/holoscan`, this can be done with the following:

```sh
(gdb) set substitute-path /usr/src/debug /holoscan/build/tmp/work/armv8a_tegra234-poky-linux
```

At this point the symbols and source code should be available and remote
debugging can begin.

> **_Note:_** This example also assumes that the application being debugged was
> written to the `armv8a_tegra234-poky-linux` directory of the build tree. This
> may need to change if the application was written to another directory
> (e.g. `armv8a-poky-linux`).
