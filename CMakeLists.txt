cmake_minimum_required(VERSION 3.16)
project(university_projects LANGUAGES CXX)

# Builds all three projects with one command:
#   cmake -S . -B build && cmake --build build
add_subdirectory(01-powerlab-gym-manager)
add_subdirectory(02-maintenance-planning-system)
add_subdirectory(03-adventures-and-dungeons)
