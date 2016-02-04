DESCRIPTION = "Lua is a powerful light-weight programming language designed \
for extending applications."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=59bdd99bb82238f238cf5c65c21604fd"
HOMEPAGE = "http://www.lua.org/"

PR = "r0"
inherit debian-package

DEPENDS = "readline libtool"

SRC_URI += "file://fix-patch-of-libtool.patch"

inherit pkgconfig binconfig

LUA_V = "5.1"
LUA_FULL_V = "5.1.5"
SONUM = "0"
LUA = "lua${LUA_V}"

PKG_DIR = "${libdir}/pkgconfig"
PKG_CONFIG_FILE = "${PKG_DIR}/lua5.1.pc"
PKG_CONFIG_FILE_FBSD = "${PKG_DIR}/lua-5.1.pc"
PKG_CONFIG_FILE_NODOT = "${PKG_DIR}/lua51.pc"
PKGPP_CONFIG_FILE = "${PKG_DIR}/lua5.1-c++.pc"
PKGPP_CONFIG_FILE_FBSD = "${PKG_DIR}/lua-5.1-c++.pc"
PKGPP_CONFIG_FILE_NODOT = "${PKG_DIR}/lua51-c++.pc"

TARGET_CC_ARCH += " -fPIC ${LDFLAGS}"
EXTRA_OEMAKE = "'CC=${CC} -fPIC' 'MYCFLAGS=${CFLAGS} -DLUA_USE_LINUX -fPIC' MYLDFLAGS='${LDFLAGS}'"

# Configure follow Debian/rules
# Create lua5.1-deb-multiarch.h file 
export LUA_MULTIARCH= "lua5.1-deb-multiarch.h"
do_configure_prepend() {
	echo "#ifndef _LUA_DEB_MULTIARCH_" > ${S}/src/${LUA_MULTIARCH}
        echo "#define _LUA_DEB_MULTIARCH_" >> ${S}/src/${LUA_MULTIARCH}
        echo "#define DEB_HOST_MULTIARCH \"$(DEB_HOST_MULTIARCH)\"" >> \
               ${S}/src/${LUA_MULTIARCH}
        echo "#endif" >> ${S}/src/${LUA_MULTIARCH}
}

do_compile () {
	oe_runmake \
		debian_linux \
		RPATH=${libdir}/$(DEB_HOST_MULTIARCH) \
		LDFLAGS="$(LDFLAGS)"
}

do_install () {
	oe_runmake \
		'INSTALL_TOP=${D}${prefix}' \
		'INSTALL_BIN=${D}${bindir}' \
		'INSTALL_INC=${D}${includedir}/' \
		'INSTALL_MAN=${D}${mandir}/man1' \
		debian_install
	install -m 644 ${S}/src/lua5.1-deb-multiarch.h ${D}${includedir}/lua5.1-deb-multiarch.h
	install -d ${D}${includedir}/${PN}/
	mv ${D}${includedir}/*.h* ${D}${includedir}/${PN}/
	
	install -d ${D}${PKG_DIR}
	echo "prefix=/usr" > ${D}${PKG_CONFIG_FILE}
	echo "major_version=${LUA_V}" >> ${D}${PKG_CONFIG_FILE}
	echo "version=${LUA_FULL_V}" >> ${D}${PKG_CONFIG_FILE}
	echo "lib_name_include=lua${LUA_V}" >> ${D}${PKG_CONFIG_FILE}
	echo "deb_host_multiarch=${DEB_HOST_MULTIARCH}" >> ${D}${PKG_CONFIG_FILE}
	cat ${S}/debian/lua.pc.in >> ${D}${PKG_CONFIG_FILE}
	ln -s $(basename ${D}${PKG_CONFIG_FILE}) ${D}${PKG_CONFIG_FILE_FBSD}
	ln -s $(basename ${D}${PKG_CONFIG_FILE}) ${D}${PKG_CONFIG_FILE_NODOT}

	echo "prefix=/usr" > ${D}${PKGPP_CONFIG_FILE}
	echo "major_version=${LUA_V}" >> ${D}${PKGPP_CONFIG_FILE}
	echo "version=${LUA_FULL_V}" >> ${D}${PKGPP_CONFIG_FILE}
	echo "lib_name_include=lua${LUA_V}" >> ${D}${PKGPP_CONFIG_FILE}
	echo "deb_host_multiarch=${DEB_HOST_MULTIARCH}" >> ${D}${PKGPP_CONFIG_FILE}
	cat ${S}/debian/lua-c++.pc.in >> ${D}${PKGPP_CONFIG_FILE}
	ln -s $(basename ${D}${PKGPP_CONFIG_FILE}) ${D}${PKGPP_CONFIG_FILE_FBSD}
	ln -s $(basename ${D}${PKGPP_CONFIG_FILE}) ${D}${PKGPP_CONFIG_FILE_NODOT}
}

PACKAGES =+ "liblua5.1-0-dev liblua5.1-0"

FILES_liblua5.1-0 = " \
	${libdir}/liblua5.1.so.0* \
	${libdir}/liblua5.1-c++.so.0* \
    "

FILES_liblua5.1-0-dev = " \
	${includedir}/${PN}/* \
	${libdir}/*.so \
	${PKG_DIR}/* \
    "

BBCLASSEXTEND = "native"
