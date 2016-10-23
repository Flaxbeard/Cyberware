#version 120

attribute vec4 a_position;
attribute vec2 a_texcoord;                                                  
varying vec2 v_texcoord;
uniform float alpha;

void main() {
    v_texcoord = a_texcoord.st;
    gl_Position = a_position;
}