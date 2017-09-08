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
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "down")
public class Down extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}")
	private File buildDirectory;

	@Parameter(defaultValue = "${project}")
	private MavenProject mavenProject;

	@Parameter(defaultValue = "${session}")
	private MavenSession mavenSession;

	@Parameter(property = "platform.version", defaultValue = "${project.version}")
	private String platformVersion;

	@Component
	private BuildPluginManager pluginManager;

	@Override
	public void execute() throws MojoExecutionException {
		getLog().info("Killing platform v" + platformVersion);
		File platformDirectory = new File(buildDirectory, "mc.varun/" + platformVersion);
		executeMojo(
			plugin(
				groupId("org.codehaus.mojo"),
				artifactId("exec-maven-plugin"),
				version("1.6.0")
			),
			goal("exec"),
			configuration(
				element("executable", "./down"),
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
