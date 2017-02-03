package li.vin.netv2.model.contract;

/** Contract implemented by any model that exists purely for the purpose of POSTing data. */
public interface ModelSeed {

  /** Throw an unchecked exception if this {@link ModelSeed} is invalid. */
  void validate();
}
