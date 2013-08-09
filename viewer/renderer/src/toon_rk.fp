
uniform int mode;
uniform vec3 eye;

varying vec3 normal;
varying vec3 light;
varying vec4 view4;

void main (void)
{
  float ndotl = max( dot(normal,light), 0.0 );
  float s = 20.0;
  float i = floor( ndotl*s )/s + 0.1;
  vec3 nv   = normalize( view4.xyz );
  float ndotv;
  if( mode == 0 ){
    ndotv= max( dot(normal,nv), 0.0 );
  } else {
    ndotv= max( dot(normal,eye), 0.0 );
  }
  ndotv= floor( ndotv*s )/s +0.1;
 
  vec4 color = vec4( 0.0, 0.0, 0.0, 1.0 );
  const vec4 black = vec4( 0.0, 0.0, 0.0, 1.0 );
  vec4 c1 = gl_FrontMaterial.ambient;
  vec4 c2 = gl_FrontMaterial.diffuse;
  vec4 c3 = gl_FrontMaterial.specular;
  vec4 color0, color1;
  if ( i > 0.80 ) {
    color0 = mix( black, c1, 0.95 );
    color1 = mix( color0, c2, 0.95 );
    color = mix( color1, c3, 0.2 );
  }else if ( i > 0.25 ) {
    color0 = mix( black, c1, 0.7 );
    color1 = mix( color0, c2, 0.7 );
    color = mix( color1, c3, 0.1 );
  }else{
    color0 = mix( black, c1, 0.4 );
    color1 = mix( color0, c2, 0.4 );
    color = mix( color1, c3, 0.05 );
  }
  // Silhouette does not work for orthogonal mode.
  if( ndotv < 0.4 ){
    color= black;
  }
  gl_FragColor = color;
}

