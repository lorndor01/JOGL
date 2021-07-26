#version 410

uniform mat4 proj_matrix;
uniform mat4 transformation_matrix;

layout(location=0) in vec2 position;
uniform vec4 input_color;

void main(void) {
	gl_Position = proj_matrix * transformation_matrix * vec4(position, 0.0, 1.0); 	
}