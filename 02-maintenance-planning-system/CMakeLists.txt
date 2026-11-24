cmake_minimum_required(VERSION 3.16)
project(maintenance_planning_system LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

file(GLOB SOURCES CONFIGURE_DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/src/*.cpp")
add_executable(maintenance_planner ${SOURCES})

# The program reads datos.txt from its working directory,
# so copy it next to the executable on every build.
configure_file(${CMAKE_CURRENT_SOURCE_DIR}/data/datos.txt
               ${CMAKE_CURRENT_BINARY_DIR}/datos.txt COPYONLY)
