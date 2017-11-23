clear

% ---------   For analyzing the log of a single session -------------

% Genius directory
genius_dir = '/home/juillermo/eclipse-workspace/The_nego_people/genius/';
% Log file to be read
log_name = 'Log-Session_20171122-234133';


%% Get agents info

oldpath = path;
% Add the path of your logs
path([genius_dir 'log'],oldpath)

tree = xmlread([log_name '.xml']);

% Retrieves agents names and domains
agents = cell(3, 4);
agent_results = tree.getElementsByTagName('resultsOfAgent');
for i=1:3
    agent_result = agent_results.item(i-1); % DOM indexes start at zero
    agent_data = agent_result.getAttributes();
    agents{i,1} = char(agent_data.getNamedItem('agent').getTextContent());
    agents{i,2} = str2double(char(agent_data.getNamedItem('finalUtility').getTextContent()));
    
    utilspace = char(agent_data.getNamedItem('utilspace').getTextContent());
    utilspace = regexp(utilspace, '(.+)\/(.+xml)$', 'tokens');
    agents{i,3} = utilspace{1,1}{1,2};
    util_dir = utilspace{1,1}{1,1};
end

%% Get utilities info

oldpath = path;
% Add the path of the utilities
path([genius_dir util_dir],oldpath)

for i=1:3
    agents{i,4} = parseProfile(agents{i,3});
end

agents

%%


%table = readtable([log_name '.csv'], 'ReadVariableNames', false);
