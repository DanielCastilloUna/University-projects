cmake_minimum_required(VERSION 3.16)
project(adventures_and_dungeons LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Static runtime only on MinGW, so the .exe runs on Windows machines
# without the MinGW DLLs. (Fully static linking is not supported on macOS.)
if (MINGW)
    set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -static")
endif()

file(GLOB_RECURSE SOURCES CONFIGURE_DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/src/*.cpp")
add_executable(adventures_and_dungeons main.cpp ${SOURCES})
target_include_directories(adventures_and_dungeons PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/include)

# The game loads data/*.txt relative to its working directory.
file(COPY ${CMAKE_CURRENT_SOURCE_DIR}/data DESTINATION ${CMAKE_CURRENT_BINARY_DIR})
