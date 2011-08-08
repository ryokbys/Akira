varying vec3 normal;
varying vec3 light;

void main (void)
{
 float i = max( dot(normalize(normal), normalize(light) ), 0.0 );
 float s = 20.0;
 i = floor( i*s )/s + 0.1;

 vec4 color = vec4( 0.0, 0.0, 0.0, 1.0 );
 vec4 c0 = vec4( 0.0, 0.0, 0.0, 1.0 );
 vec4 c1 = gl_FrontMaterial.ambient;
 vec4 c2 = gl_FrontMaterial.diffuse;
 vec4 c3 = gl_FrontMaterial.specular;
 vec4 color0, color1;
 if ( i > 0.99999 ) {
   color0 = mix( c0, c1, 0.8 );
   color1 = mix( color0, c2, 0.8 );
   color = mix( color1, c3, 0.4 );
 }
 else if ( i > 0.35 ) {
   color0 = mix( c0, c1, 0.6 );
   color1 = mix( color0, c2, 0.6 );
   color = mix( color1, c3, 0.1 );
 }
 else {
   color = c0;
 }
 gl_FragColor = color;
}

