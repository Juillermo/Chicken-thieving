import argparse

parser = argparse.ArgumentParser(description='Parse tournament logs.')
parser.add_argument('path', metavar='f', type=str, nargs=1,
                   help='path to csv file')


args = parser.parse_args()
fname=args.path[0]
def getinfo():
    log=[]
    session={}
    agents=[]
    results={}
    
    with open(fname,'r') as f:
        for line in f.readlines():
            
            if "Run" in line or "sep=;" in line:
                continue
            
                
            info=line.split(';');
            
            session={}
            session['pareto']=info[9]
            session['nash']=info[10]
            session['socialwelfare']=info[11]
            session['utilities']={}
            for agentid in range(1,4):
                agent=info[agentid+11].split('@')[0]
        
                utility=info[agentid+14]
                if agent in session['utilities'].keys():
                 session['utilities'][agent]=str(float(session['utilities'][agent])+float(utility))
                else:
                    session['utilities'][agent]=utility
                if agent not in agents:
                    agents.append(agent)
            log.append(session)
    
    return log,agents



def process(log,agents):
    results={}
    
    for agent in agents:
        results[agent]={'utility':0.0,'nash':0.0,'count':0,'normalized_utility':0.0, 'normalized_nash':0.0}

    for sess in log:
        for party in sess['utilities'].keys():
            results[party]['utility']= results[party]['utility']+float(sess['utilities'][party])
            results[party]['nash']= results[party]['nash']+float(sess['nash'])
            results[party]['count']= results[party]['count']+1
    #Using normalized values so that we can use this script even when number of each type of agent is different        
    for party in results.keys():
            if results[party]['count'] ==0:
                results[party]['normalized_utility']= 0
                results[party]['normalized_nash']=0
            else:
                
                results[party]['normalized_utility']= (results[party]['utility']/results[party]['count'])*100
                results[party]['normalized_nash']= (results[party]['nash']/results[party]['count'])*100
           
                
                

    dictlist=[]
    #Converting dict to sorted list
    for key,value in results.iteritems():
           newdict=value
           newdict['agent']=key
           dictlist.append(newdict)
    
    individual_rank = sorted(dictlist, key=lambda d: d['normalized_utility'], reverse=True)
    nash_rank= sorted(dictlist, key=lambda k: k['normalized_nash'])
    return individual_rank,nash_rank


info,agents=getinfo()
ir,nr=process(info,agents)
print "individual utility"
for a in ir:
    print a['agent']+"   "+str(a["normalized_utility"])+"\n"
print "\n Nash product"
for a in nr:
    print a['agent']+"   "+str(a["normalized_nash"])+'\n'

            
                                               
            
            
    
        

    
           
        
    
