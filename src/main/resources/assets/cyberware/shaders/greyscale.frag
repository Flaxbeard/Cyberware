#version 120

uniform float alpha; // Passed in by callback
uniform sampler2D bgl_RenderedTexture;

void main() {
    vec2 texcoord = vec2(gl_TexCoord[0]);
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
    float gs = dot(vec3(color.r, color.g, color.b), vec3(0.299, 0.587, 0.114));
    
    float rm = 0.0;
    float gm = 0.85;
    float bm = 1.0;
    
    gl_FragColor = vec4(gs * rm, gs * gm, gs * bm, color.a * alpha);
}