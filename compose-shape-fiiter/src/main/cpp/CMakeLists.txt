# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.18.1) # Or your current version

# Declares the project name.
project("composenative")

# Specifies the C++ standard.
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add your native library.
add_library(native-lib SHARED native-lib.cpp)

# Link libraries if needed, for example, the Android log library.
find_library(log-lib log)
if(log-lib)
    target_link_libraries(native-lib PRIVATE ${log-lib}) # Use PRIVATE or PUBLIC as appropriate
endif()

# Add linker flag for 16KB page alignment
if(ANDROID)
    target_link_options(native-lib PRIVATE "-Wl,-z,max-page-size=16384")
    # For older CMake versions that don't support target_link_options,
    # you might try:
    # set_target_properties(native-lib PROPERTIES LINK_FLAGS "-Wl,-z,max-page-size=16384")
    # Or, if you are setting flags globally (less ideal for this specific flag):
    # set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,-z,max-page-size=16384")
    # set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -Wl,-z,max-page-size=16384")
endif()