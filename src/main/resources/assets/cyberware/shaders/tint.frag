#version 120

uniform float alpha; // Passed in by callback
uniform float rv;
uniform float gv;
uniform float bv;

uniform sampler2D bgl_RenderedTexture;

void main() {
    vec2 texcoord = vec2(gl_TexCoord[0]);
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
    float gs = dot(vec3(color.r, color.g, color.b), vec3(0.299, 0.587, 0.114));

    gl_FragColor = vec4(gs * rv, gs * gv, gs * bv, color.a * alpha);
}