package mc.varun.pmp;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "download", inheritByDefault = false, defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class Download extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}")
	private File buildDirectory;

	@Parameter(defaultValue = "${project}")
	private MavenProject mavenProject;

	@Parameter(defaultValue = "${session}")
	private MavenSession mavenSession;

	private File platformDirectory;

	@Parameter(property = "platform.version", defaultValue = "${project.version}")
	private String platformVersion;

	@Component
	private BuildPluginManager pluginManager;

	@Override
	public void execute() throws MojoExecutionException {
		platformDirectory = new File(buildDirectory, "mc.varun/" + platformVersion);
		downloadPlatformScripts();
		setPermissions();
		downloadPlatform();
	}

	private void setPermissions() throws MojoExecutionException {
		getLog().info("Setting permissions");
		executeMojo(
			plugin(
				groupId("org.codehaus.mojo"),
				artifactId("exec-maven-plugin"),
				version("1.6.0")
			),
			goal("exec"),
			configuration(
				element("executable", "chmod"),
				element("arguments",
					element("argument", "-R"),
					element("argument", "+x"),
					element("argument", ".")
				),
				element("workingDirectory", platformDirectory.getAbsolutePath())
			),
			executionEnvironment(
				mavenProject,
				mavenSession,
				pluginManager
			)
		);
	}

	private void downloadPlatformScripts() throws MojoExecutionException {
		getLog().info("Downloading platform scripts v" + platformVersion);
		executeMojo(
			plugin(
				groupId("org.apache.maven.plugins"),
				artifactId("maven-dependency-plugin"),
				version("2.10")
			),
			goal("unpack"),
			configuration(
				element("artifactItems",
					element("artifactItem",
						element("groupId", "mc.varun"),
						element("artifactId", "platform-scripts"),
						element("version", platformVersion)
					)
				),
				element("excludes","**/META-INF/**"),
				element("outputDirectory", platformDirectory.getAbsolutePath())
			),
			executionEnvironment(
				mavenProject,
				mavenSession,
				pluginManager
			)
		);
	}

	private void downloadPlatform() throws MojoExecutionException {
		getLog().info("Downloading platform v" + platformVersion);
		executeMojo(
			plugin(
				groupId("org.codehaus.mojo"),
				artifactId("exec-maven-plugin"),
				version("1.6.0")
			),
			goal("exec"),
			configuration(
				element("executable", "./download"),
				element("workingDirectory", platformDirectory.getAbsolutePath())
			),
			executionEnvironment(
				mavenProject,
				mavenSession,
				pluginManager
			)
		);
	}
}
