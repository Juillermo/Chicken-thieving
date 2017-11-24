function profile_info = parseProfile(xml_file)
    tree = xmlread(xml_file);
    issues_tree = tree.getElementsByTagName('issue');
    weights_tree = tree.getElementsByTagName('weight');
    
    issues = cell(issues_tree.getLength, 1);
    weights = cell(weights_tree.getLength, 1);
    ind = 1;
    for j=1:issues_tree.getLength
        issue = issues_tree.item(j-1); % DOM indexes start at zero
        weight_node = weights_tree.item(j-1);
        
        issue_att = issue.getAttributes();
        if strcmp(char(issue_att.getNamedItem('vtype').getTextContent()),'discrete')
        
            items_tree = issue.getElementsByTagName('item');
            values = cell(items_tree.getLength, 1);
            evals = cell(items_tree.getLength, 1);
            for k=1:items_tree.getLength
                item_node = items_tree.item(k-1);
                item_att = item_node.getAttributes();

                evals{k} = str2double(char(item_att.getNamedItem('evaluation').getTextContent()));
                values{k} = char(item_att.getNamedItem('value').getTextContent());
            end
            issues{ind} = struct('value', values, 'eval', evals);

            weight_att = weight_node.getAttributes();
            weights{ind} = str2double(char(weight_att.getNamedItem('value').getTextContent()));
            ind = ind + 1;
        else
           issues(ind) = [];
           weights(ind) = [];
           'Non-discrete issue'
        end
    end
    
    profile_info = struct('items', issues, 'weight', weights);
end