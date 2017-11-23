function profile_info = parseProfile(xml_file)
    tree = xmlread(xml_file);
    issues_tree = tree.getElementsByTagName('issue');
    weights_tree = tree.getElementsByTagName('weight');
    
    issues = cell(issues_tree.getLength, 1);
    weights = cell(weights_tree.getLength, 1);
    for j=1:issues_tree.getLength
        issue = issues_tree.item(j-1); % DOM indexes start at zero
        weight_node = weights_tree.item(j-1);
        
        items_tree = issue.getElementsByTagName('item');
        items = cell(items_tree.getLength, 1);
        for k=1:items_tree.getLength
            item_node = items_tree.item(k-1);
            item_att = item_node.getAttributes();
            
            eval = str2double(char(item_att.getNamedItem('evaluation').getTextContent()));
            value = char(item_att.getNamedItem('value').getTextContent());
            items{k} = struct('value', value, 'eval', eval);
        end
        issues{j} = items;
        
        weight_att = weight_node.getAttributes();
        weights{j} = str2double(char(weight_att.getNamedItem('value').getTextContent()));
    end
    
    profile_info = struct('issue', issues, 'weight', weights);
end