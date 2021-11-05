package springfox.documentation.swagger2.mappers;

import com.amg.framework.boot.utils.bean.BeanCopierUtils;
import io.grpc.grpcswagger.openapi.v2.QueryParameter;
import io.grpc.grpcswagger.openapi.v2.SwaggerV2DocumentView;
import io.grpc.grpcswagger.service.DocumentService;
import io.swagger.models.Contact;

import io.swagger.models.Info;

import io.swagger.models.Model;

import io.swagger.models.Path;

import io.swagger.models.Response;

import io.swagger.models.Scheme;

import io.swagger.models.Swagger;

import io.swagger.models.Tag;

import io.swagger.models.auth.SecuritySchemeDefinition;

import io.swagger.models.parameters.Parameter;

import java.util.*;

import javax.annotation.Generated;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import springfox.documentation.service.ApiInfo;

import springfox.documentation.service.Documentation;

import springfox.documentation.service.Operation;

import springfox.documentation.service.ResourceListing;

@Generated(

        value = "org.mapstruct.ap.MappingProcessor",

        date = "2017-05-20T20:46:18-0500",

        comments = "version: 1.1.0.Final, compiler: javac, environment: Java 1.7.0_79 (Oracle Corporation)"

)

@Component

public class ServiceModelToSwagger2MapperImpl extends ServiceModelToSwagger2Mapper {

    @Autowired

    private ModelMapper modelMapper;

    @Autowired

    private ParameterMapper parameterMapper;

    @Autowired

    private SecurityMapper securityMapper;

    @Autowired

    private LicenseMapper licenseMapper;

    @Autowired

    private VendorExtensionsMapper vendorExtensionsMapper;

    @Autowired
    private DocumentService documentService;

    @Override

    public Swagger mapDocumentation(Documentation from) {

        if ( from == null ) {

            return null;
        }

        Swagger swagger = new Swagger();

        Map<String, Object> map = vendorExtensionsMapper.mapExtensions( from.getVendorExtensions() );

        if ( map != null ) {

            swagger.setVendorExtensions( map );
        }

        swagger.setHost( from.getHost() );

        List<Scheme> list = mapSchemes( from.getSchemes() );

        if ( list != null ) {

            swagger.setSchemes( list );
        }

        Map<String, Model> map_ = modelMapper.modelsFromApiListings( from.getApiListings() );

        if ( map_ != null ) {

            swagger.setDefinitions( map_ );
        }

        swagger.setInfo( mapApiInfo( fromResourceListingInfo( from ) ) );

        Map<String, Path> map__ = mapApiListings( from.getApiListings() );

        if ( map__ != null ) {

            swagger.setPaths( map__ );
        }

        Map<String, SecuritySchemeDefinition> map___ = securityMapper.toSecuritySchemeDefinitions( from.getResourceListing() );

        if ( map___ != null ) {

            swagger.setSecurityDefinitions( map___ );
        }

        swagger.setBasePath( from.getBasePath() );

        List<Tag> list_ = tagSetToTagList( from.getTags() );

        if ( list_ != null ) {

            swagger.setTags( list_ );
        }

        List<String> list__ = from.getConsumes();

        if ( list__ != null ) {

            swagger.setConsumes(       new ArrayList<String>( list__ )

            );
        }

        List<String> list___ = from.getProduces();

        if ( list___ != null ) {

            swagger.setProduces(       new ArrayList<String>( list___ )

            );
        }

        return buildSwagger(swagger);
    }

    protected Swagger buildSwagger(Swagger swagger) {
        try {
            List<SwaggerV2DocumentView> swaggerV2DocumentViews = documentService.getDocumentationCached();
            for (SwaggerV2DocumentView swaggerV2DocumentView : swaggerV2DocumentViews) {
                Swagger swaggerCopy = BeanCopierUtils.copyProperties(swaggerV2DocumentView, Swagger.class);
                swagger.getDefinitions().putAll(swaggerCopy.getDefinitions());
                swagger.getPaths().putAll(swaggerCopy.getPaths());
                swagger.getConsumes().addAll(swaggerCopy.getConsumes());
                swagger.getProduces().addAll(swaggerCopy.getProduces());
                swagger.getTags().addAll(swaggerV2DocumentView.getDocumentation().getTags());
                if (swagger.getParameters() != null && swaggerCopy.getParameters() != null) {
                    swagger.getParameters().putAll(swaggerCopy.getParameters());
                }
            }
            List<Tag> tags = swagger.getTags();
            tags = new ArrayList(new HashSet(tags));
            swagger.getTags().clear();
            swagger.setTags(tags);
        } catch (Exception e) {
        }
        return swagger;
    }

    @Override

    protected Info mapApiInfo(ApiInfo from) {

        if ( from == null ) {

            return null;
        }

        Info info_ = new Info();

        if ( info_.getVendorExtensions() != null ) {

            Map<String, Object> map = vendorExtensionsMapper.mapExtensions( from.getVendorExtensions() );

            if ( map != null ) {

                info_.getVendorExtensions().putAll( map );
            }
        }

        info_.setTermsOfService( from.getTermsOfServiceUrl() );

        info_.setLicense( licenseMapper.apiInfoToLicense( from ) );

        info_.setContact( map( from.getContact() ) );

        info_.setDescription( from.getDescription() );

        info_.setVersion( from.getVersion() );

        info_.setTitle( from.getTitle() );

        return info_;
    }

    @Override

    protected Contact map(springfox.documentation.service.Contact from) {

        if ( from == null ) {

            return null;
        }

        Contact contact_ = new Contact();

        contact_.setName( from.getName() );

        contact_.setUrl( from.getUrl() );

        contact_.setEmail( from.getEmail() );

        return contact_;
    }

    @Override

    protected io.swagger.models.Operation mapOperation(Operation from) {

        if ( from == null ) {

            return null;
        }

        io.swagger.models.Operation operation = new io.swagger.models.Operation();

        Map<String, Object> map = vendorExtensionsMapper.mapExtensions( from.getVendorExtensions() );

        if ( map != null ) {

            operation.setVendorExtensions( map );
        }

        List<Scheme> list = stringSetToSchemeList( from.getProtocol() );

        if ( list != null ) {

            operation.setSchemes( list );
        }

        operation.setDescription( from.getNotes() );

        List<Map<String, List<String>>> list_ = mapAuthorizations( from.getSecurityReferences() );

        if ( list_ != null ) {

            operation.setSecurity( list_ );
        }

        Map<String, Response> map_ = mapResponseMessages( from.getResponseMessages() );

        if ( map_ != null ) {

            operation.setResponses( map_ );
        }

        operation.setOperationId( from.getUniqueId() );

        Set<String> set = from.getTags();

        if ( set != null ) {

            operation.setTags(       new ArrayList<String>( set )

            );
        }

        operation.setSummary( from.getSummary() );

        Set<String> set_ = from.getConsumes();

        if ( set_ != null ) {

            operation.setConsumes(       new ArrayList<String>( set_ )

            );
        }

        Set<String> set__ = from.getProduces();

        if ( set__ != null ) {

            operation.setProduces(       new ArrayList<String>( set__ )

            );
        }

        List<Parameter> list__ = parameterListToParameterList( from.getParameters() );

        if ( list__ != null ) {

            operation.setParameters( list__ );
        }

        if ( from.getDeprecated() != null ) {

            operation.setDeprecated( Boolean.parseBoolean( from.getDeprecated() ) );
        }

        return operation;
    }

    @Override

    protected Tag mapTag(springfox.documentation.service.Tag from) {

        if ( from == null ) {

            return null;
        }

        Tag tag_ = new Tag();

        tag_.setName( from.getName() );

        tag_.setDescription( from.getDescription() );

        return tag_;
    }

    private ApiInfo fromResourceListingInfo(Documentation documentation) {

        if ( documentation == null ) {

            return null;
        }

        ResourceListing resourceListing = documentation.getResourceListing();

        if ( resourceListing == null ) {

            return null;
        }

        ApiInfo info = resourceListing.getInfo();

        if ( info == null ) {

            return null;
        }

        return info;
    }

    protected List<Tag> tagSetToTagList(Set<springfox.documentation.service.Tag> set) {

        if ( set == null ) {

            return null;
        }

        List<Tag> list = new ArrayList<Tag>();

        for ( springfox.documentation.service.Tag tag : set ) {

            list.add( mapTag( tag ) );
        }

        return list;
    }

    protected List<Scheme> stringSetToSchemeList(Set<String> set) {

        if ( set == null ) {

            return null;
        }

        List<Scheme> list = new ArrayList<Scheme>();

        for ( String string : set ) {

            list.add( Enum.valueOf( Scheme.class, string ) );
        }

        return list;
    }

    protected List<Parameter> parameterListToParameterList(List<springfox.documentation.service.Parameter> list) {

        if ( list == null ) {

            return null;
        }

        List<Parameter> list_ = new ArrayList<Parameter>();

        for ( springfox.documentation.service.Parameter parameter : list ) {

            list_.add( parameterMapper.mapParameter( parameter ) );
        }

        return list_;
    }
}

