#
# Base recipe: meta/recipes-graphics/xorg-driver/xf86-video-intel_2.99.910.bb
# Base branch: daisy
#

require xorg-driver-video.inc

SUMMARY = "X.Org X server -- Intel integrated graphics chipsets driver"

DESCRIPTION = "intel is an Xorg driver for Intel integrated graphics \
chipsets. The driver supports depths 8, 15, 16 and 24. On some chipsets, \
the driver supports hardware accelerated 3D via the Direct Rendering \
Infrastructure (DRI)."

PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://COPYING;md5=8730ad58d11c7bbad9a7066d69f7808e"

# Source package has no patch.
DEBIAN_QUILT_PATCHES = ""

DEPENDS += "virtual/libx11 drm libpciaccess pixman"

PACKAGECONFIG ??= "sna udev ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'dri', '', d)}"

PACKAGECONFIG[dri] = "--enable-dri,--disable-dri,xf86driproto dri2proto"
PACKAGECONFIG[sna] = "--enable-sna,--disable-sna"
PACKAGECONFIG[uxa] = "--enable-uxa,--disable-uxa"
PACKAGECONFIG[udev] = "--enable-udev,--disable-udev,udev"
PACKAGECONFIG[xvmc] = "--enable-xvmc,--disable-xvmc,libxvmc"

# --enable-kms-only option is required by ROOTLESS_X
EXTRA_OECONF += '${@base_conditional( "ROOTLESS_X", "1", " --enable-kms-only", "", d )}'

COMPATIBLE_HOST = '(i.86|x86_64).*-linux'

DPN = "xserver-xorg-video-intel"

#There is no debian patch file
DEBIAN_PATCH_TYPE = "quilt"
