varying vec2 v_texCoords;

uniform vec2 screenSize;

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;

// The inverse of the viewport dimensions along X and Y
uniform vec2 u_viewportInverse;

// Color of the outline
uniform vec3 u_color;

// Thickness of the outline
uniform float u_offset;

// Step to check for neighbors
uniform float u_step;

varying vec4 v_color;
varying vec2 v_texCoord;
void main() {
    vec4 c = texture2D(u_texture, v_texCoords) * v_color;
//    fixed4 t = texture2D(_OutlineTex, float2(v_texCoords.x + (_Time.x * _SpeedX), v_texCoords.y + (_Time.x * _SpeedY))) * v_color;
    c.rgb *= c.a;
    vec2 width = u_viewportInverse *16.0;
    // Move sprite in 4 directions according to width, we only care about the alpha
    float spriteLeft = texture2D(u_texture, v_texCoords + vec2(width.y, 0)).a;
    float spriteRight = texture2D(u_texture, v_texCoords + vec2(-width.y,  0)).a;
    float spriteBottom = texture2D(u_texture, v_texCoords + vec2( 0 ,width.x)).a;
    float spriteTop = texture2D(u_texture, v_texCoords + vec2( 0 , -width.x)).a;

    //Check all diags
    float botLeft = texture2D(u_texture, v_texCoords + vec2(-width.y, -width.x)).a;
    float botRight = texture2D(u_texture, v_texCoords + vec2(width.y,  -width.x)).a;
    float topLeft = texture2D(u_texture, v_texCoords + vec2(-width.y ,width.x)).a;
    float topRight = texture2D(u_texture, v_texCoords + vec2(width.y , width.x)).a;

    // then combine
    float result = (spriteRight + spriteLeft + spriteTop+ spriteBottom + botLeft + botRight + topLeft + topRight);
    // delete original alpha to only leave outline
    result *= (1.0-c.a);
    // add color and brightness
    vec4 outlines = result * vec4(u_color,1.0);

    #ifdef TEXTUREDOUTLINE_ON
    outlines *= t;
    #endif
    #ifdef JUSTOUTLINE_ON
    // only show outlines
    c = outlines;
    #else
    // show outlines +sprite
    c += outlines;
    #endif

    gl_FragColor = c;
}
