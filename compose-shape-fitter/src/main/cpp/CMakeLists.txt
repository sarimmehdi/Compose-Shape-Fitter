# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or higher.
cmake_minimum_required(VERSION 3.18.1) # Updated to a more modern common version

# Declares the project name.
project("composeshapefittersampleapp") # Using the module name from your context

# --- C++ Standard ---
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED True)

# --- Include Eigen ---
# Add the directory containing the "Eigen" folder to the include paths for our library.
# In this case, it's the "cpp" directory relative to this CMakeLists.txt.
# We'll use target_include_directories for better scoping.
# The path ${CMAKE_CURRENT_SOURCE_DIR} refers to the directory of this CMakeLists.txt file.
set(EIGEN_INCLUDE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/cpp)

# --- Define the Native Library ---
# Specifies the name of the library and the source files to compile.
# native-lib is a common name for Android JNI libraries.
add_library(
        composeshapefittersampleapp_native # Library name (can be anything, often matches project or module)
        SHARED                      # Specifies that this is a shared library (.so file)
        native-lib.cpp
)

# --- Include Directories for the Library ---
# Tell our library target where to find header files.
target_include_directories(composeshapefittersampleapp_native
        PRIVATE
        ${EIGEN_INCLUDE_DIR}  # So it can find <Eigen/Dense>, etc.
        # If DirectEllipseFit.h is in the cpp/ folder, and native-lib.cpp
        # includes it like #include "DirectEllipseFit.h", this might
        # implicitly work, but it's good practice to be explicit if needed,
        # especially if headers are in subdirectories of cpp/.
        # For a flat structure like cpp/DirectEllipseFit.h and cpp/native-lib.cpp,
        # the EIGEN_INCLUDE_DIR which points to 'cpp' is sufficient for Eigen
        # and also allows finding "DirectEllipseFit.h" directly if native-lib.cpp is also in cpp.
        # If native-lib.cpp was outside cpp/, then you'd definitely need:
        # ${CMAKE_CURRENT_SOURCE_DIR}/cpp # To find DirectEllipseFit.h
)
# Since EIGEN_INCLUDE_DIR already points to "cpp", it covers both finding the "Eigen"
# subdirectory and finding "DirectEllipseFit.h" from "native-lib.cpp".

# This is the key addition
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,-z,max-page-size=16384")
# For executable targets (if you had any, which is rare for NDK libraries directly):
# set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -Wl,-z,max-page-size=16384")


# --- Find and Link NDK Libraries (Common for Android) ---
# Example: Logging library (log)
find_library(
        log-lib             # Sets the name of the path variable.
        log                 # Specifies the name of the NDK library that
        # you want CMake to locate.
)

# Links the native library against NDK libraries.
if(log-lib)
    target_link_libraries(
            composeshapefittersampleapp_native
            ${log-lib}
    )
else()
    message(WARNING "Android log library not found.")
endif()

# --- Other NDK Libraries (if needed) ---
# find_library(EGL-lib EGL)
# find_library(GLESv2-lib GLESv2)
# target_link_libraries(composeshapefittersampleapp_native EGL-lib GLESv2-lib)

# You can add more libraries or compiler flags below this line.
# For example, to enable exceptions if Eigen or your code uses them:
# set_target_properties(composeshapefittersampleapp_native PROPERTIES COMPILE_FLAGS "-fexceptions")
# set_target_properties(composeshapefittersampleapp_native PROPERTIES COMPILE_FLAGS "-frtti") # If RTTI is needed

