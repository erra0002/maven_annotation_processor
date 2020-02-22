package namespace.processor;

import namespace.annotation.EmptyClassGenerator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes( "namespace.annotation.EmptyClassGenerator" )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
public class EmptyClassGeneratorProcessor extends AbstractProcessor
{
    @Override public boolean process( final Set< ? extends TypeElement > annotations, final RoundEnvironment roundEnv )
    {
        this.trace( "start processing" );

        for ( Element element : roundEnv.getElementsAnnotatedWith( EmptyClassGenerator.class ) )
        {
            this.trace( "variable name: " + element.getSimpleName() );

            if ( element.getKind() == ElementKind.CLASS )
            {
                final TypeElement classElement = ( TypeElement ) element;
                final PackageElement packageElement = ( PackageElement ) element.getEnclosingElement();

                final String qualifiedClassName = classElement.getQualifiedName() + "Generated";
                final String simpleClassName = classElement.getSimpleName() + "Generated";
                final Filer sourceFiler = this.processingEnv.getFiler();

                try ( final BufferedWriter writer = new BufferedWriter( sourceFiler.createSourceFile( qualifiedClassName ).openWriter() ) )
                {
                    writer.append( "package " ).append( packageElement.getQualifiedName() ).append( ";" );
                    writer.newLine();
                    writer.newLine();
                    writer.append( "public class " ).append( simpleClassName ).append( " {}" );
                }
                catch ( IOException e )
                {
                    this.error( "IOException: " + e.getMessage() );
                }
            }
        }

        this.trace( "finish processing" );
        return true;
    }

    private void trace( final String msg )
    {
        System.out.println( "[GENERATOR] " + msg );
    }

    private void error( final String msg )
    {
        System.err.println( "[GENERATOR] " + msg );
    }
}
