/*
 * CraftBook Copyright (C) 2010-2016 sk89q <http://www.sk89q.com>
 * CraftBook Copyright (C) 2011-2016 me4502 <http://www.me4502.com>
 * CraftBook Copyright (C) Contributors
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package com.sk89q.craftbook.core.util.documentation;

import com.sk89q.craftbook.core.CraftBookAPI;
import com.sk89q.craftbook.core.util.ConfigValue;
import com.sk89q.craftbook.core.util.PermissionNode;
import com.sk89q.craftbook.sponge.CraftBookPlugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class DocumentationGenerator {

    public static void generateDocumentation(DocumentationProvider provider) {

        File docsFile = new File(CraftBookAPI.inst().getWorkingDirectory(), "documentation");
        docsFile.mkdir();

        File docFile = new File(docsFile, provider.getPath() + ".rst");
        docFile.getParentFile().mkdirs();

        try(PrintWriter writer = new PrintWriter(docFile)) {
            writer.println(createStringOfLength(provider.getName().length(), '='));
            writer.println(provider.getName());
            writer.println(createStringOfLength(provider.getName().length(), '='));

            for(String string : provider.getMainDocumentation())
                writer.println(string);

            if(provider.getConfigurationNodes().length > 0) {

                writer.println();
                writer.println("Configuration");
                writer.println("=============");
                writer.println();

                int nodeLength = "Node".length(), commentLength = "Comment".length(), typeLength = "Type".length(), defaultLength = "Default".length();

                for(ConfigValue<?> configValue : provider.getConfigurationNodes()) {
                    if(configValue.getKey().length() > nodeLength)
                        nodeLength = configValue.getKey().length();
                    if(configValue.getComment().length() > commentLength)
                        commentLength = configValue.getComment().length();
                    if(configValue.getTypeToken().getRawType().getSimpleName().length() > typeLength)
                        typeLength = configValue.getTypeToken().getRawType().getSimpleName().length();
                    if(configValue.getDefaultValue().toString().length() > defaultLength)
                        defaultLength = configValue.getDefaultValue().toString().length();
                }

                String border = createStringOfLength(nodeLength, '=') + ' ' + createStringOfLength(commentLength, '=') + ' ' + createStringOfLength(typeLength, '=') + ' ' + createStringOfLength(defaultLength, '=');

                writer.println(border);
                writer.println(padToLength("Node", nodeLength+1) + padToLength("Comment", commentLength+1) + padToLength("Type", typeLength+1) + padToLength("Default", defaultLength+1));
                writer.println(border);
                for(ConfigValue<?> configValue : provider.getConfigurationNodes()) {
                    writer.println(padToLength(configValue.getKey(), nodeLength+1) + padToLength(configValue.getComment(), commentLength+1) + padToLength(configValue.getTypeToken().getRawType().getSimpleName(), typeLength+1) + padToLength(configValue.getDefaultValue().toString(), defaultLength+1));
                }
                writer.println(border);
            }

            if(provider.getPermissionNodes().length > 0) {

                writer.println();
                writer.println("Permissions");
                writer.println("===========");
                writer.println();

                int nodeLength = "Node".length(), descriptionLength = "Description".length(), defaultRoleLength = "Default Role".length();

                for(PermissionNode permissionNode : provider.getPermissionNodes()) {
                    if(permissionNode.getNode().length() > nodeLength)
                        nodeLength = permissionNode.getNode().length();
                    if(permissionNode.getDescription().length() > descriptionLength)
                        descriptionLength = permissionNode.getDescription().length();
                    if(permissionNode.getDefaultRole().length() > defaultRoleLength)
                        defaultRoleLength = permissionNode.getDefaultRole().length();
                }

                String border = createStringOfLength(nodeLength, '=') + ' ' + createStringOfLength(descriptionLength, '=') + ' ' + createStringOfLength(defaultRoleLength, '=');

                writer.println(border);
                writer.println(padToLength("Node", nodeLength+1) + padToLength("Description", descriptionLength+1) + padToLength("Default Role", defaultRoleLength+1));
                writer.println(border);
                for(PermissionNode permissionNode : provider.getPermissionNodes()) {
                    writer.println(padToLength(permissionNode.getNode(), nodeLength+1) + padToLength(permissionNode.getDescription(), descriptionLength+1) + padToLength(permissionNode.getDefaultRole(), defaultRoleLength+1));
                }
                writer.println(border);
            }
        } catch(IOException e) {
            CraftBookAPI.<CraftBookPlugin>inst().getLogger().error("An IO Exception occured.", e);
        }
    }

    public static String createStringOfLength(int length, char character) {
        String ret = "";

        for(int i = 0; i < length; i++)
            ret += character;

        return ret;
    }

    public static String padToLength(String string, int length) {
        while(string.length() < length)
            string += ' ';
        return string;
    }
}
