package metadata.ai.heuristics.terms;

import java.util.Arrays;
import java.util.List;

import annotations.Name;
import annotations.Opt;
import game.Game;
import game.equipment.component.Component;
import game.types.board.SiteType;
import main.collections.FVector;
import metadata.ai.heuristics.HeuristicUtil;
import metadata.ai.heuristics.transformations.HeuristicTransformation;
import metadata.ai.misc.Pair;
import other.context.Context;
import other.location.Location;
import other.state.owned.Owned;

/**
 * Defines a heuristic term based on the placement of pieces on higher layers of a game's board.
 * 
 * @author Cedric Antoine
 */
public class TopLayerProximity extends HeuristicTerm
{
	
	//-------------------------------------------------------------------------

    /** Array of names specified for piece types */
    private String[] pieceWeightNames;

    /** Array of weights as specified in metadata */
    private float[] gameAgnosticWeightsArray;

    /** Vector with weights for every piece type */
    private FVector pieceWeights = null;

	//-------------------------------------------------------------------------
    
    /**
     * Constructor
     * 
     * @param transformation An optional transformation to be applied to heuristic scores.
     * @param weight Weight for this term in a linear combination of multiple terms.
     * @param pieceWeights Weights for different piece types.
     */
    public TopLayerProximity
    (
        @Name @Opt final HeuristicTransformation transformation,
        @Name @Opt final Float weight,
        @Name @Opt final Pair[] pieceWeights
    )
    {
        super(transformation, weight);

        if (pieceWeights == null)
        {
            // Default weight of 1.0 for all
            pieceWeightNames = new String[]{""};
            gameAgnosticWeightsArray = new float[]{1.f};
        }
        else
        {
            pieceWeightNames = new String[pieceWeights.length];
            gameAgnosticWeightsArray = new float[pieceWeights.length];

            for (int i = 0; i < pieceWeights.length; ++i)
            {
                pieceWeightNames[i] = pieceWeights[i].key();
                gameAgnosticWeightsArray[i] = pieceWeights[i].floatVal();
            }
        }
    }
    
    @Override
	public HeuristicTerm copy()
	{
	    return new TopLayerProximity(this);
	}

	/**
	 * Copy constructor (private)
	 * @param other The instance to copy.
	 */
	private TopLayerProximity(final TopLayerProximity other)
	{
	    super(other.transformation, Float.valueOf(other.weight));
	    this.pieceWeightNames = Arrays.copyOf(other.pieceWeightNames, other.pieceWeightNames.length);
	    this.gameAgnosticWeightsArray = Arrays.copyOf(other.gameAgnosticWeightsArray, other.gameAgnosticWeightsArray.length);
	}
    
	//-------------------------------------------------------------------------

    @Override
    public float computeValue(final Context context, final int player, final float absWeightThreshold)
    {
        final Owned owned = context.state().owned();		
        final other.topology.Topology graph = context.topology();
        final List<? extends Location>[] pieces = owned.positions(player);

        float value = 0.f;

        for (int i = 0; i < pieces.length; ++i)
        {
            if (pieces[i].isEmpty())
                continue;

            final float pieceWeight = pieceWeights.get(owned.reverseMap(player, i));

            if (Math.abs(pieceWeight) >= absWeightThreshold)
            {
                for (final Location position : pieces[i])
                {
                	final int site = position.site();
					if (site >= graph.vertices().size())	// Different container, skip it
						continue;                	
                	final int layer = context.topology().vertices().get(site).layer();			
                    value += pieceWeight * layer; // Favor higher layers               
                }
            }
        }

        return value;
    }
    
	//-------------------------------------------------------------------------

    @Override
    public FVector computeStateFeatureVector(final Context context, final int player)
    {
        final FVector featureVector = new FVector(pieceWeights.dim());
        final Owned owned = context.state().owned();
        final other.topology.Topology graph = context.topology();
        final List<? extends Location>[] pieces = owned.positions(player);

        for (int i = 0; i < pieces.length; ++i)
        {
            final int compIdx = owned.reverseMap(player, i);

            for (final Location position : pieces[i])
            {
            	final int site = position.site();
            	if (site >= graph.vertices().size())	// Different container, skip it
					continue;
                final int layer = context.topology().vertices().get(position.site()).layer();
                featureVector.addToEntry(compIdx, layer); // Add layer to feature vector
            }
        }

        return featureVector;
    }

    @Override
    public void init(final Game game)
    {
        // Compute vector of piece weights
        pieceWeights = HeuristicTerm.pieceWeightsVector(game, pieceWeightNames, gameAgnosticWeightsArray);
    }
    
	//-------------------------------------------------------------------------
    
    @Override
   	public FVector paramsVector()
   	{
   		return pieceWeights;
   	}
    
    //-------------------------------------------------------------------------
    
    @Override
    public boolean isApplicable(final Game game)
    {
    	final Component[] components = game.equipment().components();
		
		if (components.length <= 1)
			return false;
		
    	if(game.board().defaultSite() != SiteType.Vertex)
    		return false;
    	
        return true;
    }  
    
	//-------------------------------------------------------------------------
    
    @Override
   	public String toStringThresholded(final float threshold)
   	{
   		return null;
   	}
    
	//-------------------------------------------------------------------------

    @Override
    public String description()
    {
        return "Sum of owned pieces, weighted by their layer on the board.";
    }
    
    //-------------------------------------------------------------------------
    
    @Override
    public String toEnglishString(final Context context, final int playerIndex)
    {
        final StringBuilder sb = new StringBuilder();

        if (pieceWeightNames.length > 1 || (pieceWeightNames.length == 1 && pieceWeightNames[0].length() > 0))
        {
            for (int i = 0; i < pieceWeightNames.length; ++i)
            {
                if (gameAgnosticWeightsArray[i] != 0.f)
                {
                    if (gameAgnosticWeightsArray[i] > 0)
                        sb.append("You should try to place your " + pieceWeightNames[i] + " on higher layers of the board.");
                    else
                        sb.append("You should try to place your " + pieceWeightNames[i] + " on lower layers of the board.");
                    
                    sb.append(" (" + HeuristicUtil.convertWeightToString(gameAgnosticWeightsArray[i]) + ")\n");
                }
            }
        }
        else
        {
            if (weight > 0)
                sb.append("You should try to place your pieces on higher layers of the board.");
            else
                sb.append("You should try to place your pieces on lower layers of the board.");
            
            sb.append(" (" + HeuristicUtil.convertWeightToString(weight) + ")\n");
        }

        return sb.toString();
    }
}
