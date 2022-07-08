// /////////////////////////////////////////////////////////////////////////////
// REFCODES.ORG
// /////////////////////////////////////////////////////////////////////////////
// This code is written and provided by Siegfried Steiner, Munich, Germany.
// Feel free to use it as skeleton for your own applications. Make sure you have
// considered the license conditions of the included artifacts (pom.xml).
// -----------------------------------------------------------------------------
// The REFCODES.ORG artifacts used by this template are copyright (c) by
// Siegfried Steiner, Munich, Germany and licensed under the following
// (see "http://en.wikipedia.org/wiki/Multi-licensing") licenses:
// -----------------------------------------------------------------------------
// GNU General Public License, v3.0 ("http://www.gnu.org/licenses/gpl-3.0.html")
// -----------------------------------------------------------------------------
// Apache License, v2.0 ("http://www.apache.org/licenses/LICENSE-2.0")
// -----------------------------------------------------------------------------
// Please contact the copyright holding author(s) of the software artifacts in
// question for licensing issues not being covered by the above listed licenses,
// also regarding commercial licensing models or regarding the compatibility
// with other open source licenses.
// /////////////////////////////////////////////////////////////////////////////

package tv-5;

import static org.refcodes.cli.CliSugar.*;

import java.io.UnsupportedEncodingException;

import org.refcodes.archetype.CliHelper;
import org.refcodes.cli.ArgsSyntax;
import org.refcodes.cli.ConfigOption;
import org.refcodes.cli.DebugFlag;
import org.refcodes.cli.Example;
import org.refcodes.cli.Flag;
import org.refcodes.cli.IntOption;
import org.refcodes.cli.StringOption;
import org.refcodes.cli.VerboseFlag;
import org.refcodes.data.AsciiColorPalette;
import org.refcodes.data.Scheme;
import org.refcodes.logger.RuntimeLogger;
import org.refcodes.logger.RuntimeLoggerFactorySingleton;
import org.refcodes.properties.ext.runtime.RuntimeProperties;
import org.refcodes.rest.HttpExceptionHandler;
import org.refcodes.rest.HttpRestClient;
import org.refcodes.rest.HttpRestClientImpl;
import org.refcodes.rest.HttpRestServer;
import org.refcodes.rest.HttpRestServerImpl;
import org.refcodes.rest.RestRequestEvent;
import org.refcodes.rest.RestResponse;
import org.refcodes.textual.FontFamily;
import org.refcodes.textual.Font;
import org.refcodes.textual.FontStyle;
import org.refcodes.web.HttpBodyMap;
import org.refcodes.web.HttpServerResponse;
import org.refcodes.web.HttpStatusCode;

/**
 * A minimum REFCODES.ORG enabled HTTP driven command line interface (CLI)
 * application. Get inspired by "https://bitbucket.org/funcodez".
 */
public class Main {

	// See "http://www.refcodes.org/blog/logging_like_the_nerds_log" |-->
	private static RuntimeLogger LOGGER = RuntimeLoggerFactorySingleton.createRuntimeLogger();
	// <--| See "http://www.refcodes.org/blog/logging_like_the_nerds_log"

	// /////////////////////////////////////////////////////////////////////////
	// STATICS:
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// CONSTANTS:
	// /////////////////////////////////////////////////////////////////////////

	private static final String NAME = "show";
	private static final String TITLE = ">>> " + NAME.toUpperCase() + " >>>";
	private static final String DEFAULT_CONFIG = NAME + ".ini";
	private static final char[] BANNER_PALETTE = AsciiColorPalette.MAX_LEVEL_GRAY.getPalette();
	private static final Font BANNER_FONT = new Font( FontFamily.DIALOG, FontStyle.BOLD );
	private static final String DESCRIPTION = "A minimum REFCODES.ORG enabled HTTP driven command line interface (CLI) application. Get inspired by [https://bitbucket.org/funcodez].";
	private static final String LICENSE_NOTE = "Licensed under GNU General Public License, v3.0 and Apache License, v2.0";
	private static final String COPYRIGHT = "Copyright (c) by CLUB.FUNCODES (see [https://www.funcodes.club])";
	private static final int DEFAULT_PORT = 8080;
	private static final String DEFAULT_HOST = "localhost";

	// /////////////////////////////////////////////////////////////////////////
	// VARIABLES:
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS:
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// INJECTION:
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// METHODS:
	// /////////////////////////////////////////////////////////////////////////

	public static void main( String args[] ) throws SecurityException, UnsupportedEncodingException {

		// ---------------------------------------------------------------------
		// CLI:
		// ---------------------------------------------------------------------

		// See "http://www.refcodes.org/refcodes/refcodes-cli" |-->

		StringOption thePingOption = stringOption( "--ping", "text", "Sends an HTTP GET-Request (ping) to be echoed (pong) by the RESTful server." );
		StringOption theHostOption = stringOption( "--host", "host", "The host on which the RESTful server is running (defaults to \"localhost\")." );
		IntOption thePortOption = intOption( "-p", "--port", "port", "The TCP port to be used for the HTTP echo." );
		Flag theServerFlag = flag( "--server", "server", "Starts in server mode, the server will echo according HTTP GET-Requests." );
		Flag theInitFlag = initFlag( false );
		ConfigOption theConfigOption = configOption();
		Flag theVerboseFlag = verboseFlag();
		Flag theSysInfoFlag = sysInfoFlag( false );
		Flag theHelpFlag = helpFlag();
		Flag theDebugFlag = debugFlag();

		// @formatter:off
		ArgsSyntax theArgsSyntax =  cases(
			and( thePingOption, optional( thePortOption, theHostOption, theConfigOption, theDebugFlag, theVerboseFlag ) ),
			and( theServerFlag, optional( thePortOption, theConfigOption, theDebugFlag, theVerboseFlag ) ),
			and( theInitFlag, optional( theConfigOption, theVerboseFlag) ),
			and( theSysInfoFlag, any( theVerboseFlag) ),
			theHelpFlag
		);
		Example[] theExamples = examples(
			example( "To start the REST server", theServerFlag, thePortOption, theVerboseFlag ),
			example( "To call the REST server", theHostOption, thePortOption, thePingOption, theVerboseFlag ),
			example( "Run server by using the config file (more verbose)", theServerFlag, theConfigOption, theVerboseFlag ),
			example( "Initialize default config file", theInitFlag, theVerboseFlag),
			example( "Initialize specific config file", theConfigOption, theInitFlag, theVerboseFlag),
			example( "To show the help text", theHelpFlag ),
			example( "To print the system info", theSysInfoFlag )
		);
		CliHelper theCliHelper = CliHelper.builder().
			withArgs( args ).
			withArgsSyntax( theArgsSyntax ).
			withExamples( theExamples ).
			withDefaultConfig( DEFAULT_CONFIG ). // Must be the name of the default (template) configuration file below "/src/main/resources"
			withResourceLocator( Main.class ).
			withName( NAME ).
			withTitle( TITLE ).
			withDescription( DESCRIPTION ).
			withLicenseNote( LICENSE_NOTE ).
			withCopyright( COPYRIGHT ).
			withBannerFont( BANNER_FONT ).
			withBannerPalette( BANNER_PALETTE ).
			withLogger( LOGGER ).build();
		// @formatter:on

		RuntimeProperties theArgsProperties = theCliHelper.getRuntimeProperties();

		// <--| See "http://www.refcodes.org/refcodes/refcodes-cli"

		// ---------------------------------------------------------------------
		// MAIN:
		// ---------------------------------------------------------------------

		boolean isVerbose = theCliHelper.isVerbose();
		boolean isDebug = theArgsProperties.getBoolean( theDebugFlag.getAlias() );

		if ( isVerbose ) {
			LOGGER.info( "Starting application <" + NAME + "> ..." );
		}

		if ( isVerbose ) {
			LOGGER.printSeparator();
			LOGGER.info( "Name: \"" + theArgsProperties.get( "application/name" ) + "\"" );
			LOGGER.info( "Company: \"" + theArgsProperties.get( "application/company" ) + "\"" );
			LOGGER.info( "Version: \"" + theArgsProperties.get( "application/version" ) + "\"" );
			LOGGER.printSeparator();
		}

		// ---------------------------------------------------------------------
		// REST:
		// ---------------------------------------------------------------------

		try {
			if ( theArgsProperties.getBoolean( theServerFlag.getAlias() ) ) {
				doServer( theArgsProperties );
			}
			else {
				doClient( theArgsProperties );
			}
		}
		catch ( Exception e ) {
			theCliHelper.exitOnException( e );
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// HOOKS:
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// HELPER:
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Invokes the server functionality.
	 * 
	 * @param aProperties The startup properties of the application.
	 */
	private static void doServer( RuntimeProperties aProperties ) throws Exception {
		int thePort = aProperties.getIntOr( "port", DEFAULT_PORT );
		if ( aProperties.getBoolean( "verbose" ) ) {
			LOGGER.info( "Starting server on port <" + thePort + "> ..." );
		}
		HttpRestServer theServer = new HttpRestServerImpl();
		theServer.onGet( "/ping/${text}", ( aRequest, aResponse ) -> {
			HttpBodyMap theBody = new HttpBodyMap();
			String thePing = aRequest.getWildcardReplacement( "text" );
			if ( thePing != null && thePing.length() != 0 ) {
				if ( aProperties.getBoolean( "verbose" ) ) {
					LOGGER.info( "Received ping \"" + thePing + "\", replying with pong \"" + thePing + "\"..." );
				}
				theBody.put( "pong", thePing );
				aResponse.setResponse( theBody );
			}
			else {
				theBody.put( "message", "Path element \"${ping}\" for path \"/ping/${ping}\" is missing!" );
				aResponse.setHttpStatusCode( HttpStatusCode.BAD_REQUEST );
			}
		} ).withOpen();

		theServer.onHttpException( new HttpExceptionHandler() {

			@Override
			public void onHttpError( RestRequestEvent aRequestEvent, HttpServerResponse aHttpServerResponse, Exception aException, HttpStatusCode aHttpStatusCode ) {
				if ( aProperties.getBoolean( "verbose" ) ) {
					LOGGER.warn( "Got a <" + aException.getClass().getSimpleName() + "> exception, responding with HTTP-Status-Code <" + aHttpStatusCode + "> (" + aHttpStatusCode.getStatusCode() + "> as of: " + aException.getMessage() );
				}
			}
		} );

		theServer.open( thePort );
		if ( aProperties.getBoolean( "verbose" ) ) {
			LOGGER.info( "Server started on port <" + thePort + "> ..." );
		}
		else {
			System.out.println( "Server started on port <" + thePort + "> ..." );
		}
	}

	/**
	 * Invokes the client functionality.
	 * 
	 * @param aProperties The startup properties of the application.
	 */
	private static void doClient( RuntimeProperties aProperties ) throws Exception {
		int thePort = aProperties.getIntOr( "port", DEFAULT_PORT );
		String thePing = aProperties.get( "text" );
		if ( thePing == null || thePing.length() == 0 ) {
			throw new IllegalArgumentException( "You provided an empty ping message, aborting!" );
		}
		String theHost = aProperties.getOr( "host", DEFAULT_HOST );

		if ( aProperties.getBoolean( "verbose" ) ) {
			LOGGER.info( "Sending ping to host \"" + theHost + "\" on port <" + thePort + "> with message \"" + thePing + "\"..." );
		}
		HttpRestClient theClient = new HttpRestClientImpl();
		RestResponse theResponse = theClient.doGet( Scheme.HTTP, theHost, thePort, "/ping/" + thePing );
		if ( theResponse.getHttpStatusCode().isErrorStatus() ) {
			throw theResponse.getHttpStatusCode().toHttpStatusException( theResponse.getHttpBody() );
		}
		String thePong = theResponse.getResponse().get( "pong" );
		if ( aProperties.getBoolean( "verbose" ) ) {
			LOGGER.info( "Received pong from server with message \"" + thePong + "\"." );
		}
		else {
			System.out.println( "Pong = " + thePong );
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// INNER CLASSES:
	// /////////////////////////////////////////////////////////////////////////

}
