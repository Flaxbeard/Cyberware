#version 120

uniform float alpha; // Passed in by callback
uniform sampler2D bgl_RenderedTexture;

void main() {
    gl_FragColor = texture2D(bgl_RenderedTexture, vec2(gl_TexCoord[0]));
}