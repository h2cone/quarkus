package io.quarkus.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.ProdBuildResults;
import io.quarkus.test.ProdModeTestResults;
import io.quarkus.test.QuarkusProdModeTest;

public class CustomManifestEntriesThinJarTest {

    @RegisterExtension
    static final QuarkusProdModeTest config = new QuarkusProdModeTest()
            .withEmptyApplication()
            .setApplicationName("Custom-Manifest-Thin")
            .setApplicationVersion("0.1-SNAPSHOT")
            .withConfigurationResource("projects/custom-manifest-section/custom-entries-thin.properties");

    @ProdBuildResults
    private ProdModeTestResults prodModeTestResults;

    @Test
    public void testManifestEntries() throws Exception {
        assertThat(prodModeTestResults.getResults()).hasSize(1);
        Path jarPath = prodModeTestResults.getResults().get(0).getPath();

        try (InputStream fileInputStream = new FileInputStream(jarPath.toFile())) {
            try (JarInputStream stream = new JarInputStream(fileInputStream)) {
                Manifest manifest = stream.getManifest();
                assertThat(manifest).isNotNull();

                String customAttribute = manifest.getMainAttributes().getValue("Built-By");
                assertThat(customAttribute).isNotNull();
                assertThat(customAttribute).isEqualTo("Quarkus Plugin");

                String classPath = manifest.getMainAttributes().getValue("Class-Path");
                assertThat(classPath).isNotNull();
                assertThat(classPath).startsWith("resources/ libs/a.jar");
            }
        }
    }
}
