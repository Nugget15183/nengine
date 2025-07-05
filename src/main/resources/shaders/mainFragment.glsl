#version 460 core

in vec3 fragPosition;
in vec3 fragNormal;
in vec2 fragTexCoord;

out vec4 outColor;

uniform sampler2D tex;
uniform vec3 lightDirection;
uniform vec3 lightColor;
uniform vec3 cameraPosition;
uniform float reflectiveness;
uniform float ambient;

void main() {
    vec3 normal = normalize(fragNormal);
    vec3 lightDir = normalize(-lightDirection);
    vec3 viewDir = normalize(cameraPosition - fragPosition);
    vec3 halfwayDir = normalize(lightDir + viewDir);

    float diff = max(dot(normal, lightDir), 0.0);
    float spec = pow(max(dot(normal, halfwayDir), 0.0), 32.0);

    vec3 diffuse = diff * lightColor;
    vec3 specular = spec * lightColor * reflectiveness;
    vec3 ambient = vec3(ambient);

    vec4 texColor = texture(tex, fragTexCoord);
    vec3 lighting = (ambient + diffuse + specular) * texColor.rgb;

    outColor = vec4(lighting, texColor.a);
}
