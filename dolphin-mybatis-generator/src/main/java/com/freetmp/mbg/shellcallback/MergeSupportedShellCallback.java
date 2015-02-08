package com.freetmp.mbg.shellcallback;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pin on 2015/2/7.
 */
public class MergeSupportedShellCallback extends DefaultShellCallback {

    static final Logger LOGGER = LoggerFactory.getLogger(MergeSupportedShellCallback.class);

    /**
     * @param overwrite
     */
    public MergeSupportedShellCallback(boolean overwrite) {
        super(overwrite);
    }

    @Override
    public boolean isMergeSupported() {
        return true;
    }

    /**
     * found new added ast node
     * @param newNodes
     * @param oldNodes
     * @param matcher
     * @return
     */
    public List findNewAdded(List newNodes, List oldNodes, ASTMatcher matcher){
        List added = new ArrayList<ASTNode>();

        for (Object newObject : newNodes){
            ASTNode newNode = (ASTNode) newObject;

            boolean found = false;
            for(Object oldObject : oldNodes){
                ASTNode oldNode = (ASTNode) oldObject;
                if(newNode.subtreeMatch(matcher, oldNode)){
                    found = true;
                }
            }

            if(!found){
                added.add(newNode);
            }
        }

        return added;
    }

    /**
     * check imports and add new added import to the old ast
     * @param newAst
     * @param oldAst
     * @param matcher
     */
    public void copyAndAddToOldImports(CompilationUnit newAst, CompilationUnit oldAst,ASTMatcher matcher){
        List newNodes = findNewAdded(newAst.imports(), oldAst.imports(), matcher);
        if(newNodes == null || newNodes.isEmpty()){
            LOGGER.info("There is not changes in the imports declaration");
            return;
        }
        List copies = newAst.copySubtrees(oldAst.getAST(),newNodes);
        oldAst.imports().addAll(copies);
    }

    /**
     * find the syntax equals ast node in the node list
     * @param newType
     * @param oldTypes
     * @param matcher
     * @return
     */
    public AbstractTypeDeclaration findSyntaxMatched(AbstractTypeDeclaration newType, List oldTypes, ASTMatcher matcher){
        AbstractTypeDeclaration matched = null;

        for(Object oldObject : oldTypes){
            ASTNode oldType = (ASTNode) oldObject;

            if(newType instanceof EnumDeclaration && oldType instanceof EnumDeclaration){
                if(matcher.match(newType.getName(), ((EnumDeclaration)oldType).getName())){
                    matched = (AbstractTypeDeclaration) oldType;  break;
                }
            }else if(newType instanceof TypeDeclaration && oldType instanceof TypeDeclaration){

                TypeDeclaration ntd = (TypeDeclaration) newType;
                TypeDeclaration otd = (TypeDeclaration) oldType;

                if(ntd.isInterface() && otd.isInterface()){
                    if(matcher.match(ntd.getName(), otd.getName())){
                        matched = (AbstractTypeDeclaration) oldType; break;
                    }
                }else if(!ntd.isInterface() && !otd.isInterface()){
                    if(matcher.match(ntd.getName(),otd.getName())){
                        if(matcher.safeSubtreeMatch(ntd.getSuperclassType(),otd.getSuperclassType())){
                            matched = (AbstractTypeDeclaration) oldType; break;
                        }
                    }
                }
            }else if(newType instanceof AnnotationTypeDeclaration && oldType instanceof AnnotationTypeDeclaration){
                if(matcher.match(newType.getName(), ((AnnotationTypeDeclaration) oldType).getName())){
                    matched = (AbstractTypeDeclaration) oldType; break;
                }
            }
        }

        return matched;
    }

    /**
     * find the syntax equals field declaration in the node list
     * @param newField
     * @param oldBodies
     * @param matcher
     * @return
     */
    public FieldDeclaration findSyntaxMatched(FieldDeclaration newField, List oldBodies, ASTMatcher matcher){
        FieldDeclaration matched = null;
        for(Object oldObject : oldBodies){
            if(oldObject instanceof FieldDeclaration){
                FieldDeclaration oldField = (FieldDeclaration) oldObject;
                if(matcher.safeSubtreeListMatch(newField.fragments(),oldField.fragments())){
                    matched = oldField;
                }
            }
        }
        return matched;
    }

    /**
     * find the syntax equals method declaration in the node list
     * @param newMethod
     * @param oldBodies
     * @param matcher
     * @return
     */
    public MethodDeclaration findSyntaxMatched(MethodDeclaration newMethod, List oldBodies, ASTMatcher matcher){
        MethodDeclaration matched = null;
        for(Object oldObject : oldBodies){

            // check class type if not MethodDeclaration just continue
            if(!(oldObject instanceof MethodDeclaration)){
                continue;
            }
            MethodDeclaration oldMethod = (MethodDeclaration) oldObject;

            // check method name if not match just continue
            if(!matcher.match(newMethod.getName(),oldMethod.getName())){
                continue;
            }

            boolean parametersMatch = true;
            //check method parameter
            List<SingleVariableDeclaration> newSVDs = newMethod.parameters();
            List<SingleVariableDeclaration> oldSVDs = oldMethod.parameters();

            // if two method parameter's size is not equal then continue
            if(newSVDs.size() != oldSVDs.size()) {
                parametersMatch = false;
            }

            // if two method parameter's size is not zero then check each parameter
            if(parametersMatch == true && !(newSVDs.size() == 0 && oldSVDs.size() == 0)){

                for (int i = 0; i < newSVDs.size(); i++) {
                    SingleVariableDeclaration newSVD = newSVDs.get(0);
                    SingleVariableDeclaration oldSVD = oldSVDs.get(0);
                    if(!matcher.match(newSVD.getName(),oldSVD.getName())){
                        parametersMatch = false; break;
                    }
                }
            }

            // if found matched old method just return
            if(parametersMatch){
                matched = oldMethod; break;
            }

        }
        return matched;
    }

    public void mergedToOldAst(EnumDeclaration newEnum, EnumDeclaration oldEnum, ASTMatcher matcher){
        // just ignore for now
    }

    public void mergedToOldAst(TypeDeclaration newType, TypeDeclaration oldType, ASTMatcher matcher){

        // check javadoc
        copyAndAddToOldJavadoc(newType.getJavadoc(),oldType.getJavadoc(),matcher);

        // check modifiers and add new added modifiers to the old ast
        copyAndAddToOldModifiers(newType.modifiers(),oldType.modifiers(),oldType.getAST(), matcher);

        // check super type
        if(!newType.isInterface() && !oldType.isInterface()) {
            Type newSupperClass = newType.getSuperclassType();
            Type oldSupperClass = oldType.getSuperclassType();

            if(oldSupperClass == null && newSupperClass != null){
                Type copy = (Type) newType.copySubtree(oldType.getAST(),newSupperClass);
                oldType.setSuperclassType(copy);
            }
        }

        // check super interfaces
        copyAndAddToOldSuperInterfaces(newType.superInterfaceTypes(),oldType.superInterfaceTypes(),oldType.getAST(),matcher);

        // check body declaration
        copyAndAddToOldBodies(newType.bodyDeclarations(), oldType.bodyDeclarations(), oldType.getAST(), matcher);

    }

    public void mergedToOldAst(AnnotationTypeDeclaration newAnnotation, AnnotationTypeDeclaration oldAnnotation, ASTMatcher matcher){
        // just ignore for now
    }

    /**
     * add new added changes to the old ast node
     * @param newType
     * @param oldType
     */
    public void mergedToOldAst(AbstractTypeDeclaration newType, AbstractTypeDeclaration oldType, ASTMatcher matcher){
        if(newType instanceof EnumDeclaration){ // merge enum

            mergedToOldAst((EnumDeclaration)newType,(EnumDeclaration)oldType, matcher);

        }else if(newType instanceof TypeDeclaration){ // merge type

            mergedToOldAst((TypeDeclaration)newType,(TypeDeclaration)oldType, matcher);

        }else if(newType instanceof AnnotationTypeDeclaration){ // merge annotation

            mergedToOldAst((AnnotationTypeDeclaration)newType,(AnnotationTypeDeclaration)oldType, matcher);

        }
    }

    /**
     * add new added changes to the old ast node
     * @param newField
     * @param oldField
     * @param matcher
     */
    public void mergedToOldAst(FieldDeclaration newField, FieldDeclaration oldField, ASTMatcher matcher){

        // check javadoc
        copyAndAddToOldJavadoc(newField.getJavadoc(),oldField.getJavadoc(),matcher);

        // check modifiers
        copyAndAddToOldModifiers(newField.modifiers(),oldField.modifiers(),oldField.getAST(),matcher);

    }

    /**
     * add new added changes to the old ast node
     * @param newMethod
     * @param oldMethod
     * @param matcher
     */
    public void mergedToOldAst(MethodDeclaration newMethod, MethodDeclaration oldMethod, ASTMatcher matcher){

        // check javadoc
        copyAndAddToOldJavadoc(newMethod.getJavadoc(),oldMethod.getJavadoc(),matcher);

        // check modifiers
        copyAndAddToOldModifiers(newMethod.modifiers(),oldMethod.modifiers(),oldMethod.getAST(),matcher);

        // check parameters
        List<SingleVariableDeclaration> newSVDs = newMethod.parameters();
        List<SingleVariableDeclaration> oldSVDs = oldMethod.parameters();

        for (int i = 0; i < newSVDs.size(); i++) {
            SingleVariableDeclaration newSVD = newSVDs.get(0);
            SingleVariableDeclaration oldSVD = oldSVDs.get(0);
            mergedToOldAst(newSVD,oldSVD,matcher);
        }
    }

    /**
     * add new added changes to the old ast node
     * @param newSVD
     * @param oldSVD
     * @param matcher
     */
    public void mergedToOldAst(SingleVariableDeclaration newSVD, SingleVariableDeclaration oldSVD, ASTMatcher matcher){
        //check modifiers
        copyAndAddToOldModifiers(newSVD.modifiers(),oldSVD.modifiers(),oldSVD.getAST(),matcher);
    }

    /**
     * copy and add new added javadoc tags to the old ast javadoc
     * @param newJavadoc
     * @param oldJavadoc
     * @param matcher
     */
    public void copyAndAddToOldJavadoc(Javadoc newJavadoc, Javadoc oldJavadoc, ASTMatcher matcher) {
        List newAdded = findNewAdded(newJavadoc.tags(),oldJavadoc.tags(), matcher);
        if(newAdded != null && !newAdded.isEmpty()){
            List copies = newJavadoc.copySubtrees(oldJavadoc.getAST(),newAdded);
            oldJavadoc.tags().addAll(copies);
        }
    }

    /**
     * copy and add new added modifiers to the old ast node list
     * @param newModifiers
     * @param oldModifiers
     * @param matcher
     */
    public void copyAndAddToOldModifiers(final List newModifiers, final List oldModifiers, AST oldAst, ASTMatcher matcher){

        List newAdded = findNewAdded(newModifiers,oldModifiers,matcher);
        if(newAdded != null && !newAdded.isEmpty()){
            ASTNode newNode = ((ASTNode) newAdded.get(0)).getParent();
            List copies = newNode.copySubtrees(oldAst,newAdded);
            oldModifiers.addAll(copies);
        }

    }

    /**
     * copy and add new added super interfaces to the old ast
     * @param newInterfaces
     * @param oldInterfaces
     * @param oldAst
     * @param matcher
     */
    public void copyAndAddToOldSuperInterfaces(List newInterfaces, List oldInterfaces, AST oldAst, ASTMatcher matcher) {
        List newAdded = findNewAdded(newInterfaces,oldInterfaces,matcher);
        if(newAdded != null && !newAdded.isEmpty()){
            ASTNode newNode = ((ASTNode) newAdded.get(0)).getParent();
            List copies = newNode.copySubtrees(oldAst,newAdded);
            oldInterfaces.addAll(copies);
        }
    }

    /**
     * copy and add new added body declarations to the old ast
     * @param newBodyList
     * @param oldBodyList
     * @param oldAst
     * @param matcher
     */
    public void copyAndAddToOldBodies(List newBodyList, List oldBodyList, AST oldAst, ASTMatcher matcher) {

        List<BodyDeclaration> newAdded = new ArrayList<>();

        for(Object newObject : newBodyList){
            BodyDeclaration newBd = (BodyDeclaration) newObject;
            if(newBd instanceof FieldDeclaration){

                FieldDeclaration newField = (FieldDeclaration) newBd;
                FieldDeclaration matched = findSyntaxMatched(newField,oldBodyList,matcher);
                if(matched == null){
                    newAdded.add(newField);
                    LOGGER.info("Add new field to the existed java source");
                }else{
                    mergedToOldAst(newField,matched,matcher);
                }

            }else if(newBd instanceof MethodDeclaration){

                MethodDeclaration newMethod = (MethodDeclaration) newBd;
                MethodDeclaration matched = findSyntaxMatched(newMethod,oldBodyList,matcher);
                if(matched == null){
                    newAdded.add(newMethod);
                    LOGGER.info("Add new method to the existed java source");
                }else {
                    mergedToOldAst(newMethod,matched,matcher);
                }

            }else if(newBd instanceof Initializer){

                // just ignore for now


            }else if(newBd instanceof AbstractTypeDeclaration){

                AbstractTypeDeclaration newType = (AbstractTypeDeclaration) newBd;
                AbstractTypeDeclaration matched = findSyntaxMatched(newType,oldBodyList,matcher);
                if(matched == null){
                    newAdded.add(newType);
                    LOGGER.info("Add new type to the existed java source");
                }else{
                    mergedToOldAst(newType,matched,matcher);
                }
            }
        }

    }
    /**
     * check types and add new added annotations, fields and methods to the old ast
     * @param newAst
     * @param oldAst
     * @param matcher
     */
    public void copyAndAddToOldTypes(CompilationUnit newAst, CompilationUnit oldAst, ASTMatcher matcher){
        List newTypes = newAst.types();
        List oldTypes = oldAst.types();

        List<ASTNode> newAdded = new ArrayList<>();
        for(Object newObject : newTypes){
            AbstractTypeDeclaration newType = (AbstractTypeDeclaration) newObject;
            AbstractTypeDeclaration matched = findSyntaxMatched(newType,oldTypes,matcher);

            // not found matched in the old ast, just add to the old ast
            if(matched == null){
                newAdded.add(matched);

            }else{ // merged with the equals old ast node
                mergedToOldAst(newType,matched,matcher);
            }
        }

        if(!newAdded.isEmpty()){
            List copies = newAst.copySubtrees(oldAst.getAST(),newAdded);
            oldAst.types().add(copies);
        }
    }

    @Override
    public String mergeJavaFile(String newFileSource, String existingFileFullPath, String[] javadocTags, String fileEncoding) throws ShellException {
        String mergedFileSource = newFileSource;
        try {
            CompilationUnit newAst = generateAst(newFileSource);
            CompilationUnit oldAst = generateAst(existingFileFullPath,fileEncoding);

            ASTMatcher matcher = new ASTMatcher();

            //check if there are changes existed in the package declaration
            //if not match just return the new file source
            if(!matcher.match(newAst.getPackage(),oldAst.getPackage())){
                LOGGER.info("Package declaration is different and merge is ignored");
                return newFileSource;
            };

            //check if there are changes existed in the import declarations
            // if there are new added imports then add to the old ast
            copyAndAddToOldImports(newAst, oldAst, matcher);

            //check if there are changes existed in the type declarations
            // if there are new added annotations,fields and methods then add to the old ast
            copyAndAddToOldTypes(newAst, oldAst, matcher);

            // get modified old ast
            mergedFileSource = oldAst.toString();

        } catch (Exception e) {
            throw new ShellException(e);
        }

        return mergedFileSource;
    }

    /**
     * generate abstract syntax tree from source code string
     * @param source
     * @return
     */
    protected CompilationUnit generateAst(String source){
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(source.toCharArray());
        return (CompilationUnit) parser.createAST(null);
    }

    protected CompilationUnit generateAst(String sourceFilePath, String fileEncoding) throws IOException {
        File sourceFile = new File(sourceFilePath);
        String source = FileUtils.readFileToString(sourceFile,fileEncoding);
        return generateAst(source);
    }
}
