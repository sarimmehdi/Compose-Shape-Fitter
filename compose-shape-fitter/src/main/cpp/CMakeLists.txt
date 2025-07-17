cmake_minimum_required(VERSION 3.18.1)

project("composeshapefittersampleapp_native_project")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED True)
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,-z,max-page-size=16384")

file(GLOB ELLIPSOID_FIT_SOURCES "ellipse-fit/*.cpp")
add_library(
        composeshapefittersampleapp_native
        SHARED
        native-lib.cpp
        ${ELLIPSOID_FIT_SOURCES}
)

target_include_directories(composeshapefittersampleapp_native
        PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}
)

find_library(
        log-lib
        log
)

if(log-lib)
    target_link_libraries(
            composeshapefittersampleapp_native
            PRIVATE
            ${log-lib}
    )
else()
    message(WARNING "Android log library not found.")
endif()
